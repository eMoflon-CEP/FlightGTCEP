package flight.puregt;

import static org.emoflon.flight.model.util.LongDateHelper.createTimeStamp;
import org.emoflon.flight.model.generator.SimpleModelGenerator;
import org.emoflon.flight.scenario.ScenarioRunner;

import Flights.Flight;
import Flights.FlightModel;

public class FlightGTMonitorDemo {

	public static void main(String[] args) {
		ScenarioRunner runner = new ScenarioRunner();
		runner.initModel("../Flights/src/org/emoflon/flight/model/definitions");
		runner.initModelEventGenerator(15, 12, 51, 0.01, 0.5);
		
		FlightGTMonitor monitor = new FlightGTMonitor();
		monitor.init(runner.getModel());
		monitor.initMatchSubscribers();
		monitor.update();

		
		for(int i = 0; i<2; i++) {
			runner.advanceTime();
			monitor.update();
		}
		
		monitor.shutdown();
	}

}
