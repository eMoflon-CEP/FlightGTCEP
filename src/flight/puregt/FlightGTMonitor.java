package flight.puregt;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import static org.emoflon.flight.model.util.LongDateHelper.deltaAsString;
import static org.emoflon.flight.model.util.LongDateHelper.getString_mmhhDDMMYYYY;

import FlightGTCEP.api.matches.ConnectingFlightAlternativeMatch;
import FlightGTCEP.api.matches.FlightMatch;
import FlightGTCEP.api.matches.FlightWithArrivalMatch;
import FlightGTCEP.api.matches.TravelHasConnectingFlightMatch;
import FlightGTCEP.api.matches.TravelMatch;
import FlightGTCEP.api.matches.TravelWithFlightMatch;
import Flights.Flight;
import Flights.FlightModel;
import Flights.Plane;
import Flights.Travel;
import flight.monitor.FlightMonitor;

public class FlightGTMonitor extends FlightMonitor{
	
	protected Map<Flight, Long> flight2Arrival;
	protected Map<Flight, Set<Travel>> flight2Travels;
	protected Map<Travel, Set<TravelHasConnectingFlightMatch>> travel2DelayedConnectingFlights;
	protected Map<Travel, Set<TravelHasConnectingFlightMatch>> travel2ConnectingFlights;
	protected Map<Travel, SortedSet<ConnectingFlightAlternativeMatch>> travel2AlternativeConnectingFlights;
	protected Map<Flight, Set<ConnectingFlightAlternativeMatch>> overfullAlternatives;
	
	@Override
	public void initModelAndEngine(String modelPath) {
		super.initModelAndEngine(modelPath);
		init();
	}
	
	@Override
	public void initModelAndEngine(FlightModel model) {
		super.initModelAndEngine(model);
		init();
	}
	
	private void init( ) {
		flight2Arrival = Collections.synchronizedMap(new HashMap<>());
		flight2Travels = Collections.synchronizedMap(new HashMap<>());
		travel2DelayedConnectingFlights = Collections.synchronizedMap(new HashMap<>());
		travel2ConnectingFlights  = Collections.synchronizedMap(new HashMap<>());
		travel2AlternativeConnectingFlights = Collections.synchronizedMap(new HashMap<>());
		overfullAlternatives = Collections.synchronizedMap(new HashMap<>());
		issues = Collections.synchronizedMap(new LinkedHashMap<>());
		solutions = Collections.synchronizedMap(new LinkedHashMap<>());
		
		issueMessages = new LinkedBlockingQueue<>();
		infoMessages = new LinkedBlockingQueue<>();
		solutionMessages = new LinkedBlockingQueue<>();
	}

	@Override
	public void initMatchSubscribers() {
		api.flight().subscribeAppearing(this::watchAppearingFlights);
		api.flight().subscribeDisappearing(this::watchDisappearingFlights);
		api.flightWithArrival().subscribeAppearing(this::watchFlightArrivals);
		api.travel().subscribeDisappearing(this::watchDisappearingTravels);
		api.travelWithFlight().subscribeAppearing(this::watchAppearingFlightsWithTravels);
		api.travelWithFlight().subscribeDisappearing(this::watchDisappearingFlightsWithTravels);
		api.travelHasConnectingFlight().subscribeAppearing(this::watchAppearingConnectingFlights);
		api.travelHasConnectingFlight().subscribeDisappearing(this::watchDisappearingConnectingFlights);
		api.connectingFlightAlternative().subscribeAppearing(this::watchAppearingConnectingFlightAlternatives);
		api.connectingFlightAlternative().subscribeDisappearing(this::watchDisappearingConnectingFlightAlternatives);
	}
	
	@Override
	public synchronized void update(boolean debug) {
		api.updateMatches();
		if(debug) {
			StringBuilder sb = new StringBuilder();
			FlightModel container = (FlightModel)api.getModel().getResources().get(0).getContents().get(0);
			sb.append("*** Time: " + container.getGlobalTime().getTime() + "\nOverall status:\n***");
			sb.append("\nDetected " + flight2Arrival.entrySet().stream()
					.filter(entry -> (entry.getValue() != null))
					.count()
					+ " flights overall.");
			sb.append("\nDetected " + flight2Travels.values().stream()
					.map(travels -> travels.size())
					.reduce(0, (sum, value) -> sum + value) 
					+ " flights with booked travels.");
			sb.append("\nDetected " + flight2Travels.values().stream()
					.distinct()
					.map(travels -> travels.size())
					.reduce(0, (sum, value) -> sum + value) 
					+ " travels overall.");
			sb.append("\nDetected " + travel2ConnectingFlights.values().stream()
					.map(travels -> travels.size())
					.reduce(0, (sum, value) -> sum + value)
					+ " intact connecting flights in travels overall.");
			sb.append("\nDetected " + travel2DelayedConnectingFlights.values().stream()
					.map(travels -> travels.size())
					.reduce(0, (sum, value) -> sum + value)
					+ " broken/delayed connecting flights in travels overall.");
			sb.append("\nDetected " + travel2AlternativeConnectingFlights.values().stream()
					.map(travels -> travels.size())
					.reduce(0, (sum, value) -> sum + value)
					+ " alternative connecting flights in travels overall.");

//			sb.append("\n***\nAppeared issues:\n***");
//			while(!issueMessages.isEmpty()) {
//				sb.append("\n"+issueMessages.poll());
//			}
//			
//			sb.append("\n***\nAppeared events:\n***");
//			while(!infoMessages.isEmpty()) {
//				sb.append("\n"+infoMessages.poll());
//			}
//			
//			sb.append("\n***\nAppeared solutions:\n***");
//			while(!solutionMessages.isEmpty()) {
//				sb.append("\n"+solutionMessages.poll());
//			}
			sb.append("\n***\n***");
			System.out.println(sb.toString());
		}
	}
	
	@Override
	public void shutdown() {
		api.terminate();
	}
	
	private synchronized void watchAppearingFlights(FlightMatch match) {
		flight2Travels.putIfAbsent(match.getFlight(), new HashSet<>());
		flight2Arrival.putIfAbsent(match.getFlight(), match.getFlight().getArrival().getTime());
		overfullAlternatives.put(match.getFlight(), new HashSet<>());
	}
	
	private synchronized void watchDisappearingFlights(FlightMatch match) {
		flight2Travels.remove(match.getFlight());
		flight2Arrival.remove(match.getFlight());
		overfullAlternatives.remove(match.getFlight());
	}
	
	private synchronized void watchDisappearingTravels(TravelMatch match) {
		Collection<Flight> flights = match.getTravel().getFlights();
		for(Flight flight : flights) {
			Set<Travel> travels = flight2Travels.get(flight);
			if(travels != null) {
				travels.remove(match.getTravel());
			}
		}
		travel2ConnectingFlights.remove(match.getTravel());
		
		if(travel2DelayedConnectingFlights.containsKey(match.getTravel())) {
			travel2DelayedConnectingFlights.get(match.getTravel())
			.forEach(connectingFlight -> {
				removeIssue(connectingFlight);
				infoMessages.add("Travel "+match.getTravel().getID()+" completed or canceled. Therefore, the issue concerning the delayed connecting flight "+connectingFlight.getConnectingFlight().getID()+" has been resolved.\n");
			});
		}
		travel2DelayedConnectingFlights.remove(match.getTravel());
		
		travel2AlternativeConnectingFlights.remove(match.getTravel());
		
		//TODO: Cleanup issues and solutions!
	}
	
	private synchronized void watchAppearingFlightsWithTravels(TravelWithFlightMatch match) {
		final Flight flight = match.getFlight();
		Set<Travel> travels = flight2Travels.get(flight);
		if(travels == null) {
			travels = new HashSet<>();
			flight2Travels.put(flight, travels);
		}
		travels.add(match.getTravel());
		Plane plane = match.getPlane();
		if(plane.getCapacity() <= travels.size()) {
			if(plane.getCapacity() < travels.size()) {
				addIssue(match, "Plane on flight "+flight.getID()+" is overbooked (#travels="+travels.size()+") and capacity is "+plane.getCapacity()+".");
			}else {
				infoMessages.add("Plane on flight "+flight.getID()+" is full (#travels="+travels.size()+") and capacity is "+plane.getCapacity()+".");
			}
			
			List<ConnectingFlightAlternativeMatch> removedAlternatives = travel2AlternativeConnectingFlights.values()
					.parallelStream()
					.flatMap(altMatchSet -> altMatchSet.stream())
					.filter(altMatch -> altMatch.getFlight().equals(flight))
					.collect(Collectors.toList());
			
			for(ConnectingFlightAlternativeMatch removal : removedAlternatives) {
				Set<ConnectingFlightAlternativeMatch> alternatives = travel2AlternativeConnectingFlights.get(removal.getTravel());
				if(alternatives != null) {
					alternatives.remove(removal);
					Set<ConnectingFlightAlternativeMatch> overfull = overfullAlternatives.get(flight);
					if(overfull == null) {
						overfull = new HashSet<>();
						overfullAlternatives.put(flight, overfull);
					}
					overfull.add(removal);
					//TODO: Does this become an issue?
					infoMessages.add("Travel "+removal.getTravel().getID() + " hast lost its alternative connecting flight " + removal.getReplacementFlight().getID() + " since the plane is full..\n");
				}
				
			}
		}
		
	}
	
	private synchronized void watchDisappearingFlightsWithTravels(TravelWithFlightMatch match) {
		Flight flight = match.getFlight();
		Set<Travel> travels = flight2Travels.get(flight);
		if(travels != null) {
			Plane plane = match.getPlane();
			if(plane.getCapacity() > travels.size() - 1) {
				List<ConnectingFlightAlternativeMatch> removals = new LinkedList<>();
				Set<ConnectingFlightAlternativeMatch> overfull = overfullAlternatives.get(flight);
				if(overfull != null) {
					for(ConnectingFlightAlternativeMatch alternative : overfull) {
						infoMessages.add("Travel "+alternative.getTravel().getID() + " has gained an alternative connecting flight " + alternative.getReplacementFlight().getID() + " since the plane some seats left.\n");
						removals.add(alternative);
						SortedSet<ConnectingFlightAlternativeMatch> alternatives = travel2AlternativeConnectingFlights.get(alternative.getTravel());
						if(alternatives == null) {
							alternatives = createSortedSet();
							travel2AlternativeConnectingFlights.put(alternative.getTravel(), alternatives);
						}
						alternatives.add(alternative);
						if(alternatives.first().equals(alternative)) {
							//TODO: Search for the correct connecting flight -> TravelWithFlightMatch match is the wrong type of issue match!
							addSolution(match, alternative, "Travel "+match.getTravel().getID()+" can reach its destination via alternative connecting flight "+alternative.getReplacementFlight().getID()+".\n"+
									"---> ETA: "+getString_mmhhDDMMYYYY(alternative.getFlight().getArrival().getTime()) + ", ETD: "+getString_mmhhDDMMYYYY(alternative.getReplacementDeparture().getTime()));
						}
					}
				}
				overfull.removeAll(removals);
			}
			travels.remove(match.getTravel());
		}
	}
	
	private synchronized void watchAppearingConnectingFlights(TravelHasConnectingFlightMatch match) {
		long dGates = calcGateDistance(match.getTransitAirport(), match.getArrivalGate(), match.getDepartingGate());
		long arrival = match.getFlight().getArrival().getTime();
		long departure = match.getConnectingFlight().getDeparture().getTime();
		if(arrival+dGates > departure) {
			addIssue(match, "Travel "+match.getTravel().getID()+" will miss its connecting flight "+match.getConnectingFlight().getID()+" due to a delayed arrival time.\n"+
						"---> ETA: "+getString_mmhhDDMMYYYY(arrival)+", distance to gate: "+deltaAsString(dGates)+", ETD: "+getString_mmhhDDMMYYYY(departure));
			Set<TravelHasConnectingFlightMatch> brokenFlights = travel2DelayedConnectingFlights.get(match.getTravel());
			if(brokenFlights == null) {
				brokenFlights = new HashSet<>();
				travel2DelayedConnectingFlights.put(match.getTravel(), brokenFlights);
			}
			brokenFlights.add(match);
			Set<TravelHasConnectingFlightMatch> connectingFlights = travel2ConnectingFlights.get(match.getTravel());
			if(connectingFlights != null) {
				connectingFlights.remove(match);
			}
			
			Set<ConnectingFlightAlternativeMatch> connectingAlternatives = travel2AlternativeConnectingFlights.get(match.getTravel());
			if(connectingAlternatives!=null) {
				for(ConnectingFlightAlternativeMatch connectingFlightAlternative : connectingAlternatives) {
					long dGatesAlt = calcGateDistance(match.getTransitAirport(), match.getArrivalGate(), connectingFlightAlternative.getReplacementFlight().getSrc());
					long arrivalAlt = match.getFlight().getArrival().getTime();
					long departureAlt = connectingFlightAlternative.getReplacementFlight().getDeparture().getTime();
					
					if(arrivalAlt + dGatesAlt <= departureAlt 
							&& connectingFlightAlternative.getFlight().getPlane().getCapacity() >= flight2Travels.get(connectingFlightAlternative.getFlight()).size() + 1) {
						if(match.getTransitAirport() == connectingFlightAlternative.getTransitAirport() && match.getConnectingRoute().getTrg() == connectingFlightAlternative.getConnectingRoute().getTrg()) {
							addSolution(match, connectingFlightAlternative, "Travel "+match.getTravel().getID()+" can reach its destination via alternative connecting flight "+connectingFlightAlternative.getReplacementFlight().getID()+".\n"+
									"---> ETA: "+getString_mmhhDDMMYYYY(arrivalAlt)+", distance to gate: "+deltaAsString(dGatesAlt)+", ETD: "+getString_mmhhDDMMYYYY(departureAlt));
							break;
						}						
					}
				}
			}
			
			return;
		}
		
		Set<TravelHasConnectingFlightMatch> connectingFlights = travel2ConnectingFlights.get(match.getTravel());
		if(connectingFlights == null) {
			connectingFlights = new HashSet<>();
			travel2ConnectingFlights.put(match.getTravel(), connectingFlights);
		}
		connectingFlights.add(match);
		
		Set<TravelHasConnectingFlightMatch> brokenFlights = travel2DelayedConnectingFlights.get(match.getTravel());
		if(brokenFlights != null) {
			if(brokenFlights.remove(match)) {
				infoMessages.add("Travel "+match.getTravel().getID()+" will make its connecting flight "+match.getConnectingFlight().getID()+" since the delay has been resolved.\n"+
						"---> ETA: "+getString_mmhhDDMMYYYY(arrival)+", distance to gate: "+deltaAsString(dGates)+", ETD: "+getString_mmhhDDMMYYYY(departure));
				connectingFlights.add(match);
				removeIssue(match);
			}
		}
	}
	
	private synchronized void watchDisappearingConnectingFlights(TravelHasConnectingFlightMatch match) {
		Set<TravelHasConnectingFlightMatch> brokenFlights = travel2DelayedConnectingFlights.get(match.getTravel());
		if(brokenFlights == null) {
			brokenFlights = new HashSet<>();
			travel2DelayedConnectingFlights.put(match.getTravel(), brokenFlights);
		}
		addIssue(match, "Travel "+match.getTravel().getID()+" will miss its connecting flight "+match.getConnectingFlight().getID());
		brokenFlights.add(match);
		
		Set<TravelHasConnectingFlightMatch> connectingFlights = travel2ConnectingFlights.get(match.getTravel());
		if(connectingFlights != null) {
			connectingFlights.remove(match);
		}
		
		Set<ConnectingFlightAlternativeMatch> connectingAlternatives = travel2AlternativeConnectingFlights.get(match.getTravel());
		if(connectingAlternatives!=null) {
			for(ConnectingFlightAlternativeMatch connectingFlightAlternative : connectingAlternatives) {
				long dGatesAlt = calcGateDistance(match.getTransitAirport(), match.getArrivalGate(), connectingFlightAlternative.getReplacementFlight().getSrc());
				long arrivalAlt = match.getFlight().getArrival().getTime();
				long departureAlt = connectingFlightAlternative.getReplacementFlight().getDeparture().getTime();
				if(flight2Travels.get(connectingFlightAlternative.getFlight()) == null)
					continue;
				
				if(arrivalAlt + dGatesAlt <= departureAlt 
						&& connectingFlightAlternative.getFlight().getPlane().getCapacity() >= flight2Travels.get(connectingFlightAlternative.getFlight()).size() + 1) {
					if(match.getTransitAirport() == connectingFlightAlternative.getTransitAirport() && match.getConnectingRoute().getTrg() == connectingFlightAlternative.getConnectingRoute().getTrg()) {
						addSolution(match, connectingFlightAlternative, "Travel "+match.getTravel().getID()+" can reach its destination via alternative connecting flight "+connectingFlightAlternative.getReplacementFlight().getID()+".\n"+
								"---> ETA: "+getString_mmhhDDMMYYYY(arrivalAlt)+", distance to gate: "+deltaAsString(dGatesAlt)+", ETD: "+getString_mmhhDDMMYYYY(departureAlt));
						break;
					}						
				}
			}
		}
	}
	
	private synchronized void watchFlightArrivals(FlightWithArrivalMatch match) {
		Long prevArrival = flight2Arrival.get(match.getFlight());
		if(prevArrival == null)
			return;
		
		
		Long currentArrival = match.getFlight().getArrival().getTime();
		flight2Arrival.replace(match.getFlight(), currentArrival);
		Set<Travel> travels = flight2Travels.get(match.getFlight());
		if(travels == null)
			return;
		
		// check travels in case of a early arrival
		if(currentArrival<prevArrival) {
			infoMessages.add("Flight "+match.getFlight().getID()+" will arrive early by "+ deltaAsString(prevArrival-currentArrival) +"minutes.\n"+
					"---> current ETA: "+getString_mmhhDDMMYYYY(currentArrival)+", original ETA: "+getString_mmhhDDMMYYYY(prevArrival));
			
			for(Travel travel : travels) {
				Set<TravelHasConnectingFlightMatch> brokenFlights =  travel2DelayedConnectingFlights.get(travel);
				if(brokenFlights == null)
					continue;
				
				Set<TravelHasConnectingFlightMatch> removedFlights = new HashSet<>();
				
				for(TravelHasConnectingFlightMatch brokenFlight : brokenFlights) {
					long dGates = calcGateDistance(brokenFlight.getTransitAirport(), brokenFlight.getArrivalGate(), brokenFlight.getDepartingGate());
					long arrival = brokenFlight.getFlight().getArrival().getTime();
					long departure = brokenFlight.getConnectingFlight().getDeparture().getTime();
					
					if(arrival+dGates <= departure) {
						infoMessages.add("Travel "+brokenFlight.getTravel().getID()+" will now make its connecting flight "+brokenFlight.getConnectingFlight().getID()+" since the delay has been resolved.\n"+
								"---> ETA: "+getString_mmhhDDMMYYYY(arrival)+", distance to gate: "+deltaAsString(dGates)+", ETD: "+getString_mmhhDDMMYYYY(departure));
						removeIssue(brokenFlight);
						//TODO: Delete issues!
						Set<TravelHasConnectingFlightMatch> connectedFlights = travel2ConnectingFlights.get(brokenFlight.getTravel());
						if(connectedFlights == null) {
							connectedFlights = new HashSet<>();
							travel2DelayedConnectingFlights.put(brokenFlight.getTravel(), connectedFlights);
						}
						connectedFlights.add(brokenFlight);
						removedFlights.add(brokenFlight);
					}
				}
				
				brokenFlights.removeAll(removedFlights);

			}
		}
		// check travels in case of a delay
		else if(currentArrival>prevArrival) {
			infoMessages.add("Flight "+match.getFlight().getID()+" currently has a delay of "+ deltaAsString(currentArrival-prevArrival) +"minutes.\n"+
					"---> current ETA: "+getString_mmhhDDMMYYYY(currentArrival)+", original ETA: "+getString_mmhhDDMMYYYY(prevArrival));
			
			for(Travel travel : travels) {
				Set<TravelHasConnectingFlightMatch> connectingFlights = travel2ConnectingFlights.get(travel);
				if(connectingFlights == null)
					continue;
				
				Set<TravelHasConnectingFlightMatch> removedFlights = new HashSet<>();
				
				for(TravelHasConnectingFlightMatch connectingFlight : connectingFlights) {
					long dGates = calcGateDistance(connectingFlight.getTransitAirport(), connectingFlight.getArrivalGate(), connectingFlight.getDepartingGate());
					long arrival = connectingFlight.getFlight().getArrival().getTime();
					long departure = connectingFlight.getConnectingFlight().getDeparture().getTime();
					
					if(arrival+dGates > departure) {
						addIssue(connectingFlight, "Travel "+connectingFlight.getTravel().getID()+" will miss its connecting flight "+connectingFlight.getConnectingFlight().getID()+" due to a delayed arrival time.\n"+
									"---> ETA: "+getString_mmhhDDMMYYYY(arrival)+", distance to gate: "+deltaAsString(dGates)+", ETD: "+getString_mmhhDDMMYYYY(departure));
						Set<TravelHasConnectingFlightMatch> brokenFlights = travel2DelayedConnectingFlights.get(connectingFlight.getTravel());
						if(brokenFlights == null) {
							brokenFlights = new HashSet<>();
							travel2DelayedConnectingFlights.put(connectingFlight.getTravel(), brokenFlights);
						}
						brokenFlights.add(connectingFlight);
						removedFlights.add(connectingFlight);
						
						Set<ConnectingFlightAlternativeMatch> connectingAlternatives = travel2AlternativeConnectingFlights.get(connectingFlight.getTravel());
						if(connectingAlternatives!=null) {
							for(ConnectingFlightAlternativeMatch connectingFlightAlternative : connectingAlternatives) {
								long dGatesAlt = calcGateDistance(connectingFlight.getTransitAirport(), connectingFlight.getArrivalGate(), connectingFlightAlternative.getReplacementFlight().getSrc());
								long arrivalAlt = connectingFlightAlternative.getFlight().getArrival().getTime();
								long departureAlt = connectingFlightAlternative.getReplacementFlight().getDeparture().getTime();
								
								if(flight2Travels.get(connectingFlightAlternative.getFlight()) == null)
									continue;
								
								if(arrivalAlt + dGatesAlt <= departureAlt 
										&& connectingFlightAlternative.getFlight().getPlane().getCapacity() >= flight2Travels.get(connectingFlightAlternative.getFlight()).size() + 1) {
									if(connectingFlight.getTransitAirport() == connectingFlightAlternative.getTransitAirport() && connectingFlight.getConnectingRoute().getTrg() == connectingFlightAlternative.getConnectingRoute().getTrg()) {
										addSolution(connectingFlight, connectingFlightAlternative, "Travel "+connectingFlight.getTravel().getID()+" can reach its destination via alternative connecting flight "+connectingFlightAlternative.getReplacementFlight().getID()+".\n"+
												"---> ETA: "+getString_mmhhDDMMYYYY(arrivalAlt)+", distance to gate: "+deltaAsString(dGatesAlt)+", ETD: "+getString_mmhhDDMMYYYY(departureAlt));
										break;
									}						
								}
							}
						}
					}
				}
				
				connectingFlights.removeAll(removedFlights);
				
				
			}
		}
			
	}
	
	private synchronized void watchAppearingConnectingFlightAlternatives(ConnectingFlightAlternativeMatch match) {
		SortedSet<ConnectingFlightAlternativeMatch> alternatives = travel2AlternativeConnectingFlights.get(match.getTravel());
		if(alternatives == null) {
			alternatives = createSortedSet();
			travel2AlternativeConnectingFlights.put(match.getTravel(), alternatives);
		}
		
		long dGates = calcGateDistance(match.getTransitAirport(), match.getArrivalGate(), match.getReplacementFlight().getSrc());
		long arrival = match.getFlight().getArrival().getTime();
		long departure = match.getReplacementFlight().getDeparture().getTime();
		
		if(arrival + dGates <= departure 
				&& match.getFlight().getPlane().getCapacity() >= flight2Travels.get(match.getFlight()).size() + 1) {
			alternatives.add(match);
			Set<TravelHasConnectingFlightMatch> brokenConnectingFlights = travel2DelayedConnectingFlights.get(match.getTravel());
			if(brokenConnectingFlights != null && match.equals(alternatives.first())) {
				for(TravelHasConnectingFlightMatch brokenConnectingFlight : brokenConnectingFlights) {
					if(match.getConnectingRoute() == brokenConnectingFlight.getConnectingRoute()) {
						addSolution(brokenConnectingFlight, match, "Travel "+brokenConnectingFlight.getTravel().getID()+" can reach its destination via alternative connecting flight "+match.getReplacementFlight().getID()+".\n"+
								"---> ETA: "+getString_mmhhDDMMYYYY(arrival)+", distance to gate: "+deltaAsString(dGates)+", ETD: "+getString_mmhhDDMMYYYY(departure));
					}
				}
			}
				
		}
	}
	
	private synchronized void watchDisappearingConnectingFlightAlternatives(ConnectingFlightAlternativeMatch match) {
		Set<ConnectingFlightAlternativeMatch> alternatives = travel2AlternativeConnectingFlights.get(match.getTravel());
		if(alternatives != null) {
			alternatives.remove(match);
		}
	}

	@Override
	public long getWorkingConnectingFlightTravels() {
		return travel2ConnectingFlights.values().stream().flatMap(set -> set.stream()).collect(Collectors.toSet()).size();
	}

	@Override
	public long getDelayedConnectingFlightTravels() {
		return travel2DelayedConnectingFlights.values().stream().flatMap(set -> set.stream()).collect(Collectors.toSet()).size();
	}

}
