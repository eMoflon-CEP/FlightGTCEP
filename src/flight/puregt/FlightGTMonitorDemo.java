package flight.puregt;

import org.emoflon.flight.scenario.EvaluationScenarioRunner;

import flight.monitor.FlightMonitor;
import flight.util.Runtimer;
import hipe.generic.actor.junction.util.HiPEConfig;

public class FlightGTMonitorDemo {

	public static void main(String[] args) {
//		HiPEConfig.loggingActivated = true;
		EvaluationScenarioRunner runner = new EvaluationScenarioRunner(15,1);
		runner.initModel("../Flights/src/org/emoflon/flight/model/definitions");
		runner.initModelEventGenerator(15, 12, 51, 0.01, 0.5);
		
		FlightGTMonitor monitor = new FlightGTMonitor();
		monitor.initModelAndEngine(runner.getModel());
		monitor.initMatchSubscribers();
		
		Runtimer timer = Runtimer.getInstance();
		timer.measure(FlightGTMonitor.class, "FlightGTRun", ()->run(monitor, runner));
		monitor.shutdown();
		
		
		System.out.println(timer.toString());
	}
	
	public static void run(FlightMonitor monitor, EvaluationScenarioRunner runner) {
		Runtimer timer = Runtimer.getInstance();
		monitor.update(true);

		double days = 6.0;
		double delta = 0.5;
		boolean runnable = true;
		while(runnable && days>0) {
			timer.pause();
			runnable = runner.runForDays(delta);
			timer.resume();
			
			monitor.update(true);
			days-=delta;
		}
		
		monitor.update(true);
		
		System.err.println("Broken connecting flights: " + monitor.getDelayedConnectingFlightTravels());
		System.err.println("Working connecting flights: " + monitor.getWorkingConnectingFlightTravels());
		System.err.println("Issues: " + monitor.getIssues().size());
	}

}
