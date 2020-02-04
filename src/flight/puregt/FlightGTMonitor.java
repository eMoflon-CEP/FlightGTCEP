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
import FlightGTCEP.api.matches.TravelHasConnectingFlightMatch;
import FlightGTCEP.api.matches.TravelWithFlightMatch;
import Flights.Flight;
import Flights.Person;
import Flights.Plane;
import Flights.Travel;

public class FlightGTMonitor {
	protected FlightGTCEPApp app;
	protected FlightGTCEPAPI api;
	
	protected Map<Flight, Set<Travel>> flight2Travels;
	protected Map<Travel, Set<TravelHasConnectingFlightMatch>> travel2DelayedConnectingFlights;
	protected List<String> warnings;
	protected List<String> infos;
	
	public void init(String modelPath) {
		app = new FlightGTCEPHiPEApp();
		app.registerMetaModels();
		URI uri = URI.createFileURI(modelPath);
		app.loadModel(uri);
		api = app.initAPI();
		
		flight2Travels = new HashMap<>();
		travel2DelayedConnectingFlights = new HashMap<>();
		warnings = new LinkedList<>();
		infos = new LinkedList<>();
	}

	public void initMatchSubscribers() {
		api.flight().subscribeAppearing(this::watchAppearingFlights);
		api.flight().subscribeDisappearing(this::watchDisappearingFlights);
		api.travelWithFlight().subscribeAppearing(this::watchAppearingPersons);
		api.travelWithFlight().subscribeDisappearing(this::watchDisappearingPersons);
		api.travelHasConnectingFlight().subscribeAppearing(this::watchAppearingConnectingFlights);
	}
	
	private void watchAppearingFlights(FlightMatch match) {
		flight2Travels.putIfAbsent(match.getFlight(), new HashSet<>());
	}
	
	private void watchDisappearingFlights(FlightMatch match) {
		flight2Travels.remove(match.getFlight());
	}
	
	private void watchAppearingPersons(TravelWithFlightMatch match) {
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
	
	private void watchDisappearingPersons(TravelWithFlightMatch match) {
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
			return;
		}
		
		Set<TravelHasConnectingFlightMatch> brokenFlights = travel2DelayedConnectingFlights.get(match.getTravel());
		if(brokenFlights != null) {
			if(brokenFlights.remove(match)) {
				infos.add("Travel "+match.getTravel().getID()+" will make its connecting flight "+match.getConnectingFlight().getID()+" since the delay has been resolved.\n"+
						"---> ETA: "+arrival+", distance to gate: "+dGates+", ETD: "+departure);
			}
		}
	}

}