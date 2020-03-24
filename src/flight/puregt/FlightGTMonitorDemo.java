package flight.puregt;

import org.emoflon.flight.scenario.EvaluationScenarioRunner;
import org.emoflon.flight.scenario.ScenarioRunner;

import flight.monitor.FlightMonitor;
import flight.util.Runtimer;

public class FlightGTMonitorDemo {

	public static void main(String[] args) {
//		ScenarioRunner runner = new ScenarioRunner();
		ScenarioRunner runner = new EvaluationScenarioRunner(15);
		runner.initModel("../Flights/src/org/emoflon/flight/model/definitions");
		runner.initModelEventGenerator(15, 12, 51, 0.01, 0.5);
		
		FlightGTMonitor monitor = new FlightGTMonitor();
		monitor.initModelAndEngine(runner.getModel());
		monitor.initMatchSubscribers();
//		monitor.update(true);
//
//		int i = 0;
//		while(runner.advanceTime() &&  i < 20) {
//			monitor.update(true);
//			i++;
//		}
//		
//		monitor.update(true);
//		
//		System.err.println("Broken connecting flights: " + monitor.getDelayedConnectingFlightTravels());
//		System.err.println("Working connecting flights: " + monitor.getWorkingConnectingFlightTravels());
//		System.err.println("Issues: " + monitor.getIssues().size());
		
		Runtimer timer = Runtimer.getInstance();
		timer.measure(FlightGTMonitor.class, "FlightGTRun", ()->run(monitor, runner));
		monitor.shutdown();
		
		
		System.out.println(timer.toString());
	}
	
	public static void run(FlightMonitor monitor, ScenarioRunner runner) {
		monitor.update(true);

		int i = 0;
		while(runner.advanceTime() &&  i < 20) {
			monitor.update(true);
			i++;
		}
		
		monitor.update(true);
//		monitor.getDelayedConnectingFlightTravels();
//		monitor.getWorkingConnectingFlightTravels();
//		monitor.getIssues();
		
		System.err.println("Broken connecting flights: " + monitor.getDelayedConnectingFlightTravels());
		System.err.println("Working connecting flights: " + monitor.getWorkingConnectingFlightTravels());
		System.err.println("Issues: " + monitor.getIssues().size());
	}

}
