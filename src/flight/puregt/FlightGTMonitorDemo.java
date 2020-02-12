package flight.puregt;

import static org.emoflon.flight.model.util.LongDateHelper.createTimeStamp;
import org.emoflon.flight.model.generator.SimpleModelGenerator;

import Flights.Flight;
import Flights.FlightModel;

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
		
		connectingFlight.setArrival(createTimeStamp(connectingFlight.getArrival(), 60, true));
		monitor.update();

		connectingFlight.setArrival(createTimeStamp(connectingFlight.getArrival(), 60, false));
		monitor.update();
		
		monitor.shutdown();
	}

}
