package flight.puregt;

import org.emoflon.flight.model.generator.SimpleModelGenerator;

import Flights.Flight;
import Flights.FlightModel;
import Flights.FlightsFactory;
import Flights.TimeStamp;

public class FlightGTMonitorDemo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SimpleModelGenerator generator = new SimpleModelGenerator();
		FlightModel model = generator.generateSimpleModel("../Flights/src/org/emoflon/flight/model/definitions");
		FlightGTMonitor monitor = new FlightGTMonitor();
		monitor.init(model);
		monitor.initMatchSubscribers();
		monitor.update();
		Flight connectingFlight = monitor.travel2ConnectingFlights.values().stream()
				.filter(matchSet -> !matchSet.isEmpty())
				.findAny().get().iterator().next().getFlight();
		
		TimeStamp arrival = FlightsFactory.eINSTANCE.createTimeStamp();
		arrival.setTime(connectingFlight.getArrival().getTime()+100000000);
		connectingFlight.setArrival(arrival);
		monitor.update();
		
		TimeStamp arrival2 = FlightsFactory.eINSTANCE.createTimeStamp();
		arrival2.setTime(connectingFlight.getArrival().getTime()-100000000);
		connectingFlight.setArrival(arrival2);
		monitor.update();
		
		monitor.shutdown();
	}

}
