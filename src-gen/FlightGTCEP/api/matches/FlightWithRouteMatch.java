package FlightGTCEP.api.matches;

import FlightGTCEP.api.rules.FlightWithRoutePattern;
import Flights.Flight;
import Flights.Route;
import org.emoflon.ibex.common.operational.IMatch;
import org.emoflon.ibex.gt.api.GraphTransformationMatch;

/**
 * A match for the pattern <code>flightWithRoute()</code>.
 */
public class FlightWithRouteMatch extends GraphTransformationMatch<FlightWithRouteMatch, FlightWithRoutePattern> {
	private Route varRoute;
	private Flight varFlight;

	/**
	 * Creates a new match for the pattern <code>flightWithRoute()</code>.
	 * 
	 * @param pattern
	 *            the pattern
	 * @param match
	 *            the untyped match
	 */
	public FlightWithRouteMatch(final FlightWithRoutePattern pattern, final IMatch match) {
		super(pattern, match);
		varRoute = (Route) match.get("route");
		varFlight = (Flight) match.get("flight");
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

	@Override
	public String toString() {
		String s = "match {" + System.lineSeparator();
		s += "	route --> " + varRoute + System.lineSeparator();
		s += "	flight --> " + varFlight + System.lineSeparator();
		s += "} for " + getPattern();
		return s;
	}
}
