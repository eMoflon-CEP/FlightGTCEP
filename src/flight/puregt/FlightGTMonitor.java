package flight.puregt;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;

import FlightGTCEP.api.FlightGTCEPAPI;
import FlightGTCEP.api.FlightGTCEPApp;
import FlightGTCEP.api.FlightGTCEPHiPEApp;
import FlightGTCEP.api.matches.FlightMatch;
import FlightGTCEP.api.matches.FlightWithArrivalMatch;
import FlightGTCEP.api.matches.TravelHasConnectingFlightMatch;
import FlightGTCEP.api.matches.TravelMatch;
import FlightGTCEP.api.matches.TravelWithFlightMatch;
import Flights.Flight;
import Flights.FlightModel;
import Flights.Plane;
import Flights.TimeStamp;
import Flights.Travel;

public class FlightGTMonitor {
	protected FlightGTCEPApp app;
	protected FlightGTCEPAPI api;
	
	protected Map<Flight, Long> flight2Arrival;
	protected Map<Flight, Set<Travel>> flight2Travels;
	protected Map<Travel, Set<TravelHasConnectingFlightMatch>> travel2DelayedConnectingFlights;
	protected Map<Travel, Set<TravelHasConnectingFlightMatch>> travel2ConnectingFlights;
	protected List<String> warnings;
	protected List<String> infos;
	
	public void init(String modelPath) {
		app = new FlightGTCEPHiPEApp();
		app.registerMetaModels();
		URI uri = URI.createFileURI(modelPath);
		app.loadModel(uri);
		api = app.initAPI();
		
		flight2Arrival = new HashMap<>();
		flight2Travels = new HashMap<>();
		travel2DelayedConnectingFlights = new HashMap<>();
		travel2ConnectingFlights  = new HashMap<>();
		
		warnings = new LinkedList<>();
		infos = new LinkedList<>();
	}
	
	public void init(FlightModel model) {
		app = new FlightGTCEPHiPEApp();
		app.registerMetaModels();
		app.createModel(URI.createFileURI(model.toString()));
		app.getModel().getResources().get(0).getContents().add(model);
		api = app.initAPI();
		
		flight2Arrival = new HashMap<>();
		flight2Travels = new HashMap<>();
		travel2DelayedConnectingFlights = new HashMap<>();
		travel2ConnectingFlights  = new HashMap<>();
		
		warnings = new LinkedList<>();
		infos = new LinkedList<>();
	}

	public void initMatchSubscribers() {
		api.flight().subscribeAppearing(this::watchAppearingFlights);
		api.flight().subscribeDisappearing(this::watchDisappearingFlights);
		api.flightWithArrival().subscribeAppearing(this::watchFlightArrivals);
		api.travel().subscribeDisappearing(this::watchDisappearingTravels);
		api.travelWithFlight().subscribeAppearing(this::watchAppearingFlightsWithTravels);
		api.travelWithFlight().subscribeDisappearing(this::watchDisappearingFlightsWithTravels);
		api.travelHasConnectingFlight().subscribeAppearing(this::watchAppearingConnectingFlights);
		api.travelHasConnectingFlight().subscribeDisappearing(this::watchDisappearingConnectingFlights);
	}
	
	public void update() {
		api.updateMatches();
		System.out.println("Detected "+flight2Arrival.size()+" flights overall.");
		System.out.println("Detected "+flight2Travels.size()+" flights with booked travels.");
		System.out.println("Detected "+flight2Travels.values().stream().map(travels -> travels.size()).reduce(0, (sum, value) -> sum + value)+" travels overall.");
		System.out.println("Detected "+travel2ConnectingFlights.values().stream().map(travels -> travels.size()).reduce(0, (sum, value) -> sum + value)+" intact connecting flights in travels overall.");
		System.out.println("*** Appeared warnings and conflicts:");
		while(!warnings.isEmpty()) {
			System.out.println(((LinkedList<String>)warnings).poll());
		}
		System.out.println("*** Appeared infos and resolved conflicts:");
		while(!infos.isEmpty()) {
			System.out.println(((LinkedList<String>)infos).poll());
		}
	}
	
	public void shutdown() {
		api.terminate();
	}
	
	private void watchAppearingFlights(FlightMatch match) {
		flight2Travels.putIfAbsent(match.getFlight(), new HashSet<>());
		flight2Arrival.putIfAbsent(match.getFlight(), match.getFlight().getArrival().getTime());
	}
	
	private void watchDisappearingFlights(FlightMatch match) {
		flight2Travels.remove(match.getFlight());
		flight2Arrival.remove(match.getFlight());
	}
	
	private void watchDisappearingTravels(TravelMatch match) {
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
				infos.add("Travel "+match.getTravel().getID()+" completed or canceled. Therefore, the issue concerning the delayed connecting flight "+connectingFlight.getConnectingFlight().getID()+" has been resolved.\n");
			});
		}
	}
	
	private void watchAppearingFlightsWithTravels(TravelWithFlightMatch match) {
		Flight flight = match.getFlight();
		Set<Travel> travels = flight2Travels.get(flight);
		if(travels == null) {
			travels = new HashSet<>();
			flight2Travels.put(flight, travels);
		}
		travels.add(match.getTravel());
		Plane plane = match.getPlane();
		if(plane.getCapacity()<travels.size()) {
			warnings.add("Plane on flight "+flight.getID()+" is overbooked(#travels="+travels.size()+") but capacity is "+plane.getCapacity()+".");
		}
	}
	
	private void watchDisappearingFlightsWithTravels(TravelWithFlightMatch match) {
		Flight flight = match.getFlight();
		Set<Travel> travels = flight2Travels.get(flight);
		if(travels != null) {
			travels.remove(match.getTravel());
		}
	}
	
	private void watchAppearingConnectingFlights(TravelHasConnectingFlightMatch match) {
		int dGates = Math.abs(match.getArrivalGate().getPosition()-match.getDepartingGate().getPosition());
		long arrival = match.getFlight().getArrival().getTime();
		long departure = match.getConnectingFlight().getDeparture().getTime();
		if(arrival+dGates > departure) {
			warnings.add("Travel "+match.getTravel().getID()+" will miss its connecting flight "+match.getConnectingFlight().getID()+" due to a delayed arrival time.\n"+
						"---> ETA: "+arrival+", distance to gate: "+dGates+", ETD: "+departure);
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
				infos.add("Travel "+match.getTravel().getID()+" will make its connecting flight "+match.getConnectingFlight().getID()+" since the delay has been resolved.\n"+
						"---> ETA: "+arrival+", distance to gate: "+dGates+", ETD: "+departure);
				connectingFlights.add(match);
			}
		}
	}
	
	private void watchDisappearingConnectingFlights(TravelHasConnectingFlightMatch match) {
		Set<TravelHasConnectingFlightMatch> brokenFlights = travel2DelayedConnectingFlights.get(match.getTravel());
		if(brokenFlights == null) {
			brokenFlights = new HashSet<>();
			travel2DelayedConnectingFlights.put(match.getTravel(), brokenFlights);
		}
		warnings.add("Travel "+match.getTravel().getID()+" will miss its connecting flight "+match.getConnectingFlight().getID());
		brokenFlights.add(match);
		
		Set<TravelHasConnectingFlightMatch> connectingFlights = travel2ConnectingFlights.get(match.getTravel());
		if(connectingFlights != null) {
			connectingFlights.remove(match);
		}
	}
	
	private void watchFlightArrivals(FlightWithArrivalMatch match) {
		Long prevArrival = flight2Arrival.get(match.getFlight());
		if(prevArrival == null)
			return;
		
		Long currentArrival = match.getFlight().getArrival().getTime();
		Set<Travel> travels = flight2Travels.get(match.getFlight());
		if(travels == null)
			return;
		
		// check travels in case of a early arrival
		if(currentArrival<prevArrival) {
			infos.add("Flight "+match.getFlight().getID()+" will arrive early by "+ (prevArrival-currentArrival) +"minutes.\n"+
					"---> current ETA: "+currentArrival+", original ETA: "+prevArrival);
			
			for(Travel travel : travels) {
				Set<TravelHasConnectingFlightMatch> brokenFlights =  travel2DelayedConnectingFlights.get(travel);
				if(brokenFlights == null)
					continue;
				
				Set<TravelHasConnectingFlightMatch> removedFlights = new HashSet<>();
				
				for(TravelHasConnectingFlightMatch brokenFlight : brokenFlights) {
					int dGates = Math.abs(brokenFlight.getArrivalGate().getPosition()-brokenFlight.getDepartingGate().getPosition());
					long arrival = brokenFlight.getFlight().getArrival().getTime();
					long departure = brokenFlight.getConnectingFlight().getDeparture().getTime();
					
					if(arrival+dGates <= departure) {
						infos.add("Travel "+brokenFlight.getTravel().getID()+" will now make its connecting flight "+brokenFlight.getConnectingFlight().getID()+" since the delay has been resolved.\n"+
								"---> ETA: "+arrival+", distance to gate: "+dGates+", ETD: "+departure);
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
			warnings.add("Flight "+match.getFlight().getID()+" currently has a delay of "+ (currentArrival-prevArrival) +"minutes.\n"+
					"---> current ETA: "+currentArrival+", original ETA: "+prevArrival);
			
			for(Travel travel : travels) {
				Set<TravelHasConnectingFlightMatch> connectingFlights = travel2ConnectingFlights.get(travel);
				if(connectingFlights == null)
					continue;
				
				Set<TravelHasConnectingFlightMatch> removedFlights = new HashSet<>();
				
				for(TravelHasConnectingFlightMatch connectingFlight : connectingFlights) {
					int dGates = Math.abs(connectingFlight.getArrivalGate().getPosition()-connectingFlight.getDepartingGate().getPosition());
					long arrival = connectingFlight.getFlight().getArrival().getTime();
					long departure = connectingFlight.getConnectingFlight().getDeparture().getTime();
					
					if(arrival+dGates > departure) {
						warnings.add("Travel "+connectingFlight.getTravel().getID()+" will miss its connecting flight "+connectingFlight.getConnectingFlight().getID()+" due to a delayed arrival time.\n"+
									"---> ETA: "+arrival+", distance to gate: "+dGates+", ETD: "+departure);
						Set<TravelHasConnectingFlightMatch> brokenFlights = travel2DelayedConnectingFlights.get(connectingFlight.getTravel());
						if(brokenFlights == null) {
							brokenFlights = new HashSet<>();
							travel2DelayedConnectingFlights.put(connectingFlight.getTravel(), brokenFlights);
						}
						brokenFlights.add(connectingFlight);
						removedFlights.add(connectingFlight);
					}
				}
				
				connectingFlights.removeAll(removedFlights);
				
				
			}
		}
			
	}

}
