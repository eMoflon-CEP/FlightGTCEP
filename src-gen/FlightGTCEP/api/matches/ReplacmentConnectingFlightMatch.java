package FlightGTCEP.api.matches;

import FlightGTCEP.api.rules.ReplacmentConnectingFlightPattern;
import Flights.Airport;
import Flights.Flight;
import Flights.Gate;
import Flights.Route;
import Flights.Travel;
import org.emoflon.ibex.common.operational.IMatch;
import org.emoflon.ibex.gt.api.GraphTransformationMatch;

/**
 * A match for the pattern <code>replacmentConnectingFlight(travelID, oldFlightID)</code>.
 */
public class ReplacmentConnectingFlightMatch extends GraphTransformationMatch<ReplacmentConnectingFlightMatch, ReplacmentConnectingFlightPattern> {
	private Gate varArrivalGate;
	private Flight varConectingFlight;
	private Route varConectingRoute;
	private Gate varDepartingGate;
	private Flight varFlight;
	private Flight varReplacementFlight;
	private Route varReplacementRoute;
	private Route varRoute;
	private Airport varTargetAirport;
	private Airport varTransitAirport;
	private Travel varTravel;

	/**
	 * Creates a new match for the pattern <code>replacmentConnectingFlight(travelID, oldFlightID)</code>.
	 * 
	 * @param pattern
	 *            the pattern
	 * @param match
	 *            the untyped match
	 */
	public ReplacmentConnectingFlightMatch(final ReplacmentConnectingFlightPattern pattern, final IMatch match) {
		super(pattern, match);
		varArrivalGate = (Gate) match.get("arrivalGate");
		varConectingFlight = (Flight) match.get("conectingFlight");
		varConectingRoute = (Route) match.get("conectingRoute");
		varDepartingGate = (Gate) match.get("departingGate");
		varFlight = (Flight) match.get("flight");
		varReplacementFlight = (Flight) match.get("replacementFlight");
		varReplacementRoute = (Route) match.get("replacementRoute");
		varRoute = (Route) match.get("route");
		varTargetAirport = (Airport) match.get("targetAirport");
		varTransitAirport = (Airport) match.get("transitAirport");
		varTravel = (Travel) match.get("travel");
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
	 * Returns the conectingFlight.
	 *
	 * @return the conectingFlight
	 */
	public Flight getConectingFlight() {
		return varConectingFlight;
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
	 * Returns the departingGate.
	 *
	 * @return the departingGate
	 */
	public Gate getDepartingGate() {
		return varDepartingGate;
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
	 * Returns the replacementFlight.
	 *
	 * @return the replacementFlight
	 */
	public Flight getReplacementFlight() {
		return varReplacementFlight;
	}

	/**
	 * Returns the replacementRoute.
	 *
	 * @return the replacementRoute
	 */
	public Route getReplacementRoute() {
		return varReplacementRoute;
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
	 * Returns the targetAirport.
	 *
	 * @return the targetAirport
	 */
	public Airport getTargetAirport() {
		return varTargetAirport;
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
		s += "	arrivalGate --> " + varArrivalGate + System.lineSeparator();
		s += "	conectingFlight --> " + varConectingFlight + System.lineSeparator();
		s += "	conectingRoute --> " + varConectingRoute + System.lineSeparator();
		s += "	departingGate --> " + varDepartingGate + System.lineSeparator();
		s += "	flight --> " + varFlight + System.lineSeparator();
		s += "	replacementFlight --> " + varReplacementFlight + System.lineSeparator();
		s += "	replacementRoute --> " + varReplacementRoute + System.lineSeparator();
		s += "	route --> " + varRoute + System.lineSeparator();
		s += "	targetAirport --> " + varTargetAirport + System.lineSeparator();
		s += "	transitAirport --> " + varTransitAirport + System.lineSeparator();
		s += "	travel --> " + varTravel + System.lineSeparator();
		s += "} for " + getPattern();
		return s;
	}
}
