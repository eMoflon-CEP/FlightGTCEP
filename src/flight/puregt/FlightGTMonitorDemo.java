package flight.puregt;

import org.emoflon.flight.scenario.EvaluationScenarioRunner;
import org.emoflon.flight.scenario.ScenarioRunner;

public class FlightGTMonitorDemo {

	public static void main(String[] args) {
//		ScenarioRunner runner = new ScenarioRunner();
		ScenarioRunner runner = new EvaluationScenarioRunner(10);
		runner.initModel("../Flights/src/org/emoflon/flight/model/definitions");
		runner.initModelEventGenerator(15, 12, 51, 0.01, 0.5);
		
		FlightGTMonitor monitor = new FlightGTMonitor();
		monitor.initModelAndEngine(runner.getModel());
		monitor.initMatchSubscribers();
		monitor.update(true);

		int i = 0;
		while(runner.advanceTime() &&  i < 20) {
			monitor.update(true);
			i++;
		}
		
		monitor.update(true);
		
		System.err.println("Broken connecting flights: " + monitor.getDelayedConnectingFlightTravels());
		System.err.println("Working connecting flights: " + monitor.getWorkingConnectingFlightTravels());
		System.err.println("Issues: " + monitor.getIssues().size());
		monitor.shutdown();
	}

}
