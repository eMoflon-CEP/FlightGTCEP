package flight.test;

import static org.junit.Assert.assertEquals;

import org.emoflon.flight.model.util.LongDateHelper;
import org.junit.After;
import org.junit.Test;

import FlightGTCEP.api.FlightGTCEPAPI;
import FlightGTCEP.api.FlightGTCEPApp;
import Flights.Flight;
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
	public void testFindConnectingFlights1() {
		init(instanceFolder+"/test1.xmi");
		monitor.update(false);
		assertEquals(2, monitor.getWorkingConnectingFlightTravels().size());
		assertEquals(0, monitor.getDelayedConnectingFlightTravels().size());
	}
	
	@Test
	public void testDelayConnectingFlights1() {
		init(instanceFolder+"/test1.xmi");
		monitor.update(false);
		
		Flight flight = model.getBookings().getBookings().stream()
							.filter(booking -> booking.getID()
							.contains("Rick&Morty"))
							.findAny().get().getTravels().get(0).getFlights().get(0);
		
		delayFlight(flight, 20);
		monitor.update(false);
		assertEquals(0, monitor.getWorkingConnectingFlightTravels().size());
		assertEquals(2, monitor.getDelayedConnectingFlightTravels().size());

	}
	
	@Test
	public void testPromoteConnectingFlights1() {
		init(instanceFolder+"/test1.xmi");
		monitor.update(false);
		
		Flight flight = model.getBookings().getBookings().stream()
							.filter(booking -> booking.getID()
							.contains("Rick&Morty"))
							.findAny().get().getTravels().get(0).getFlights().get(0);
		
		promoteFlight(flight, 20);
		monitor.update(false);
		assertEquals(2, monitor.getWorkingConnectingFlightTravels().size());
		assertEquals(0, monitor.getDelayedConnectingFlightTravels().size());

	}
	
	@Test
	public void testDelayPromoteConnectingFlights1() {
		init(instanceFolder+"/test1.xmi");
		monitor.update(false);
		
		Flight flight = model.getBookings().getBookings().stream()
							.filter(booking -> booking.getID()
							.contains("Rick&Morty"))
							.findAny().get().getTravels().get(0).getFlights().get(0);
		
		delayFlight(flight, 20);
		monitor.update(false);
		assertEquals(0, monitor.getWorkingConnectingFlightTravels().size());
		assertEquals(2, monitor.getDelayedConnectingFlightTravels().size());
		
		promoteFlight(flight, 20);
		monitor.update(false);
		assertEquals(2, monitor.getWorkingConnectingFlightTravels().size());
		assertEquals(0, monitor.getDelayedConnectingFlightTravels().size());

	}
	
	@Test
	public void testAlternativeFlights1() {
		init(instanceFolder+"/test1.xmi");
		monitor.update(false);
		
		Flight flight = model.getBookings().getBookings().stream()
							.filter(booking -> booking.getID()
							.contains("Rick&Morty"))
							.findAny().get().getTravels().get(0).getFlights().get(0);
		
		delayFlight(flight, 20);
		monitor.update(false);
		assertEquals(0, monitor.getWorkingConnectingFlightTravels().size());
		assertEquals(2, monitor.getDelayedConnectingFlightTravels().size());
		assertEquals(2, monitor.getIssues().size());
		assertEquals(2, monitor.getSolutions().size());
		
		promoteFlight(flight, 20);
		monitor.update(false);
		assertEquals(2, monitor.getWorkingConnectingFlightTravels().size());
		assertEquals(0, monitor.getDelayedConnectingFlightTravels().size());
		assertEquals(0, monitor.getIssues().size());
		assertEquals(0, monitor.getSolutions().size());

	}
	
	@After
	public void shutdown() {
		monitor.shutdown();
	}
	
	public static void delayFlight(final Flight flight, int delayMins) {
		flight.setArrival(LongDateHelper.createTimeStamp(flight.getArrival(), delayMins, true));
	}
	
	public static void promoteFlight(final Flight flight, int promoteMins) {
		flight.setArrival(LongDateHelper.createTimeStamp(flight.getArrival(), promoteMins, false));
	}

}
