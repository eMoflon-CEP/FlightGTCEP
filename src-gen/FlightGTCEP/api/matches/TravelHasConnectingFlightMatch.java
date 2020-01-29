package FlightGTCEP.api.matches;

import FlightGTCEP.api.rules.TravelHasConnectingFlightPattern;
import Flights.Airport;
import Flights.Flight;
import Flights.Gate;
import Flights.Route;
import Flights.Travel;
import org.emoflon.ibex.common.operational.IMatch;
import org.emoflon.ibex.gt.api.GraphTransformationMatch;

/**
 * A match for the pattern <code>travelHasConnectingFlight()</code>.
 */
public class TravelHasConnectingFlightMatch extends GraphTransformationMatch<TravelHasConnectingFlightMatch, TravelHasConnectingFlightPattern> {
	private Airport varTransitAirport;
	private Gate varArrivalGate;
	private Gate varDepartingGate;
	private Route varRoute;
	private Flight varFlight;
	private Route varConectingRoute;
	private Flight varConectingFlight;
	private Travel varTravel;

	/**
	 * Creates a new match for the pattern <code>travelHasConnectingFlight()</code>.
	 * 
	 * @param pattern
	 *            the pattern
	 * @param match
	 *            the untyped match
	 */
	public TravelHasConnectingFlightMatch(final TravelHasConnectingFlightPattern pattern, final IMatch match) {
		super(pattern, match);
		varTransitAirport = (Airport) match.get("transitAirport");
		varArrivalGate = (Gate) match.get("arrivalGate");
		varDepartingGate = (Gate) match.get("departingGate");
		varRoute = (Route) match.get("route");
		varFlight = (Flight) match.get("flight");
		varConectingRoute = (Route) match.get("conectingRoute");
		varConectingFlight = (Flight) match.get("conectingFlight");
		varTravel = (Travel) match.get("travel");
	}

	/**
	 * Returns the transitAirport.
	 *
	 * @return the transitAirport
	 */
	public Airport getTransitAirport() {
		return varTransitAirport;
	}

	/**
	 * Returns the arrivalGate.
	 *
	 * @return the arrivalGate
	 */
	public Gate getArrivalGate() {
		return varArrivalGate;
	}

	/**
	 * Returns the departingGate.
	 *
	 * @return the departingGate
	 */
	public Gate getDepartingGate() {
		return varDepartingGate;
	}

	/**
	 * Returns the route.
	 *
	 * @return the route
	 */
	public Route getRoute() {
		return varRoute;
	}

	/**
	 * Returns the flight.
	 *
	 * @return the flight
	 */
	public Flight getFlight() {
		return varFlight;
	}

	/**
	 * Returns the conectingRoute.
	 *
	 * @return the conectingRoute
	 */
	public Route getConectingRoute() {
		return varConectingRoute;
	}

	/**
	 * Returns the conectingFlight.
	 *
	 * @return the conectingFlight
	 */
	public Flight getConectingFlight() {
		return varConectingFlight;
	}

	/**
	 * Returns the travel.
	 *
	 * @return the travel
	 */
	public Travel getTravel() {
		return varTravel;
	}

	@Override
	public String toString() {
		String s = "match {" + System.lineSeparator();
		s += "	transitAirport --> " + varTransitAirport + System.lineSeparator();
		s += "	arrivalGate --> " + varArrivalGate + System.lineSeparator();
		s += "	departingGate --> " + varDepartingGate + System.lineSeparator();
		s += "	route --> " + varRoute + System.lineSeparator();
		s += "	flight --> " + varFlight + System.lineSeparator();
		s += "	conectingRoute --> " + varConectingRoute + System.lineSeparator();
		s += "	conectingFlight --> " + varConectingFlight + System.lineSeparator();
		s += "	travel --> " + varTravel + System.lineSeparator();
		s += "} for " + getPattern();
		return s;
	}
}
