package flight.puregt;

import static org.emoflon.flight.model.util.LongDateHelper.createTimeStamp;
import org.emoflon.flight.model.generator.SimpleModelGenerator;
import org.emoflon.flight.scenario.ScenarioRunner;

import Flights.Flight;
import Flights.FlightModel;

public class FlightGTMonitorDemo {

	public static void main(String[] args) {
//		SimpleModelGenerator generator = new SimpleModelGenerator();
//		FlightModel model = generator.generateSimpleModel("../Flights/src/org/emoflon/flight/model/definitions");
		ScenarioRunner runner = new ScenarioRunner();
		runner.initModel("../Flights/src/org/emoflon/flight/model/definitions");
		runner.initModelEventGenerator(15, 12, 51, 0.01);
		
		FlightGTMonitor monitor = new FlightGTMonitor();
		monitor.init(runner.getModel());
		monitor.initMatchSubscribers();
		monitor.update();
//		Flight connectingFlight = monitor.travel2ConnectingFlights.values().stream()
//				.filter(matchSet -> !matchSet.isEmpty())
//				.flatMap(matchSet -> matchSet.stream())
//				.filter(match -> monitor.travel2AlternativeConnectingFlights.containsKey(match.getTravel()))
//				.filter(match -> !monitor.travel2AlternativeConnectingFlights.get(match.getTravel()).isEmpty())
//				.findAny().get().getFlight();
//		
//		connectingFlight.setArrival(createTimeStamp(connectingFlight.getArrival(), 220, true));
//		monitor.update();
//
//		connectingFlight.setArrival(createTimeStamp(connectingFlight.getArrival(), 220, false));
//		monitor.update();
//		
//		monitor.shutdown();
		
		for(int i = 0; i<100; i++) {
			runner.advanceTime();
			monitor.update();
		}
		
		monitor.shutdown();
	}

}
