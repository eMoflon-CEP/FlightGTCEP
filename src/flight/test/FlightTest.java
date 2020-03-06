package flight.test;

import static org.junit.Assert.assertEquals;

import java.util.NoSuchElementException;

import org.emoflon.flight.model.util.LongDateHelper;
import org.junit.After;
import org.junit.Test;

import FlightGTCEP.api.FlightGTCEPAPI;
import FlightGTCEP.api.FlightGTCEPApp;
import FlightGTCEP.api.matches.ConnectingFlightAlternativeMatch;
import Flights.Booking;
import Flights.Flight;
import Flights.FlightModel;
import Flights.Travel;
import flight.monitor.FlightMonitor;

public abstract class FlightTest {
	
	protected FlightGTCEPApp app;
	protected FlightGTCEPAPI api;
	protected FlightModel model;
	protected FlightMonitor monitor;
	
	protected String instanceFolder = "../FlightGTCEP/instances";
	
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
		assertEquals(2, monitor.getWorkingConnectingFlightTravels());
		assertEquals(0, monitor.getDelayedConnectingFlightTravels());
	}
	
	@Test
	public void testDelayConnectingFlights1() {
		init(instanceFolder+"/test1.xmi");
		monitor.update(false);
		
		Flight flight = getFlight(model, "MUC->FRA_1");
		
		delayFlight(flight, 20);
		monitor.update(false);
		assertEquals(0, monitor.getWorkingConnectingFlightTravels());
		assertEquals(2, monitor.getDelayedConnectingFlightTravels());

	}
	
	@Test
	public void testPromoteConnectingFlights1() {
		init(instanceFolder+"/test1.xmi");
		monitor.update(false);
		
		Flight flight = getFlight(model, "MUC->FRA_1");
		
		promoteFlight(flight, 20);
		monitor.update(false);
		assertEquals(2, monitor.getWorkingConnectingFlightTravels());
		assertEquals(0, monitor.getDelayedConnectingFlightTravels());

	}
	
	@Test
	public void testDelayPromoteConnectingFlights1() {
		init(instanceFolder+"/test1.xmi");
		monitor.update(false);
		
		Flight flight = getFlight(model, "MUC->FRA_1");
		
		delayFlight(flight, 20);
		monitor.update(false);
		assertEquals(0, monitor.getWorkingConnectingFlightTravels());
		assertEquals(2, monitor.getDelayedConnectingFlightTravels());
		
		promoteFlight(flight, 20);
		monitor.update(false);
		assertEquals(2, monitor.getWorkingConnectingFlightTravels());
		assertEquals(0, monitor.getDelayedConnectingFlightTravels());

	}
	
	@Test
	public void testAlternativeFlights1() {
		init(instanceFolder+"/test1.xmi");
		monitor.update(false);
		
		Flight flight = getFlight(model, "MUC->FRA_1");
		
		delayFlight(flight, 20);
		monitor.update(false);
		assertEquals(0, monitor.getWorkingConnectingFlightTravels());
		assertEquals(2, monitor.getDelayedConnectingFlightTravels());
		assertEquals(2, monitor.getIssues().size());
		assertEquals(2, monitor.getSolutions().size());
		
		promoteFlight(flight, 20);
		monitor.update(false);
		assertEquals(2, monitor.getWorkingConnectingFlightTravels());
		assertEquals(0, monitor.getDelayedConnectingFlightTravels());
		assertEquals(0, monitor.getIssues().size());
		assertEquals(0, monitor.getSolutions().size());

	}
	
	@Test
	public void testAlternativeFlightsOverbooked1() {
		init(instanceFolder+"/test1.xmi");
		monitor.update(false);
		
		Flight flight = getFlight(model, "MUC->FRA_1");
		
		delayFlight(flight, 20);
		monitor.update(false);
		assertEquals(0, monitor.getWorkingConnectingFlightTravels());
		assertEquals(2, monitor.getDelayedConnectingFlightTravels());
		assertEquals(2, monitor.getIssues().size());
		assertEquals(2, monitor.getSolutions().size());
		
		ConnectingFlightAlternativeMatch alternative = (ConnectingFlightAlternativeMatch)monitor.getSolutions().iterator().next().solutionMatch;
		Travel travel = alternative.getTravel();
		Flight brokenFlight = alternative.getFlight();
		Flight alternativeFlight = alternative.getReplacementFlight();
		travel.getFlights().remove(brokenFlight);
		travel.getFlights().add(alternativeFlight);
		
		monitor.update(true);
		
		assertEquals(1, monitor.getWorkingConnectingFlightTravels());
		assertEquals(1, monitor.getDelayedConnectingFlightTravels());
		assertEquals(1, monitor.getIssues().size());
		assertEquals(0, monitor.getSolutions().size());
		
	}
	
	@After
	public void shutdown() {
		monitor.shutdown();
	}
	
	public static Booking getBooking(final FlightModel model, final String idFragment) {
		return model.getBookings().getBookings().parallelStream()
				.filter(booking -> booking.getID().contains(idFragment))
				.findFirst().get();
	}
	
	public static Travel getTravel(final FlightModel model, final String idFragment) {
		return model.getBookings().getBookings().parallelStream()
				.flatMap(booking -> booking.getTravels().parallelStream())
				.filter(travel -> travel.getID().contains(idFragment))
				.findFirst().get();
	}
	
	public static Flight getFlight(final FlightModel model, final String idFragment) {
		return model.getFlights().getFlights().parallelStream()
				.filter(flight -> flight.getID().contains(idFragment))
				.findFirst().get();
	}
	
	public static void delayFlight(final Flight flight, int delayMins) {
		flight.setArrival(LongDateHelper.createTimeStamp(flight.getArrival(), delayMins, true));
	}
	
	public static void promoteFlight(final Flight flight, int promoteMins) {
		flight.setArrival(LongDateHelper.createTimeStamp(flight.getArrival(), promoteMins, false));
	}

}
