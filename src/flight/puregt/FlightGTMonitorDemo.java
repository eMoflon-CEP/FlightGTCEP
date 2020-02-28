package flight.puregt;

import org.emoflon.flight.scenario.ScenarioRunner;

public class FlightGTMonitorDemo {

	public static void main(String[] args) {
		ScenarioRunner runner = new ScenarioRunner();
		runner.initModel("../Flights/src/org/emoflon/flight/model/definitions");
		runner.initModelEventGenerator(15, 12, 51, 0.01, 0.5);
		
		FlightGTMonitor monitor = new FlightGTMonitor();
		monitor.initModelAndEngine(runner.getModel());
		monitor.initMatchSubscribers();
		monitor.update(true);

		while(runner.advanceTime()) {
			monitor.update(true);
		}
		monitor.update(true);
		
		monitor.shutdown();
	}

}
