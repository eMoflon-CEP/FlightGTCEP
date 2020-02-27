package flight.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import FlightGTCEP.api.FlightGTCEPAPI;
import FlightGTCEP.api.FlightGTCEPApp;
import Flights.FlightModel;
import flight.monitor.FlightMonitor;

public abstract class FlightTest {
	
	protected FlightGTCEPApp app;
	protected FlightGTCEPAPI api;
	protected FlightModel model;
	protected FlightMonitor monitor;
	
	protected String instanceFolder = "instances";
	
	public abstract FlightMonitor getMonitor();
	
	public void init(String modelPath) {
		monitor = getMonitor();
		monitor.initModelAndEngine(modelPath);
		monitor.initMatchSubscribers();
		app = monitor.getApp();
		api = monitor.getApi();
		model = monitor.getModel();
	}
	
	@Test
	public void testFindConnectingFlights() {
		init(instanceFolder+"/test1.xmi");
		monitor.update(false);
		assertEquals(2, monitor.getWorkingConnectingFlightTravels().size());
	}

}
