package FlightGTCEP.api.rules;

import FlightGTCEP.api.FlightGTCEPAPI;
import FlightGTCEP.api.matches.FlightWithRouteMatch;
import Flights.Flight;
import Flights.Route;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.emoflon.ibex.common.operational.IMatch;
import org.emoflon.ibex.gt.api.GraphTransformationPattern;
import org.emoflon.ibex.gt.engine.GraphTransformationInterpreter;

/**
 * The pattern <code>flightWithRoute()</code> which does the following:
 * track new/updated flights in a pattern for Apama to filter delayed flights
 */
public class FlightWithRoutePattern extends GraphTransformationPattern<FlightWithRouteMatch, FlightWithRoutePattern> {
	private static String patternName = "flightWithRoute";

	/**
	 * Creates a new pattern flightWithRoute().
	 * 
	 * @param api
	 *            the API the pattern belongs to
	 * @param interpreter
	 *            the interpreter
	 */
	public FlightWithRoutePattern(final FlightGTCEPAPI api, final GraphTransformationInterpreter interpreter) {
		super(api, interpreter, patternName);
	}

	@Override
	protected FlightWithRouteMatch convertMatch(final IMatch match) {
		return new FlightWithRouteMatch(this, match);
	}

	@Override
	protected List<String> getParameterNames() {
		List<String> names = new ArrayList<String>();
		names.add("route");
		names.add("flight");
		return names;
	}

	/**
	 * Binds the node route to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public FlightWithRoutePattern bindRoute(final Route object) {
		parameters.put("route", Objects.requireNonNull(object, "route must not be null!"));
		return this;
	}

	/**
	 * Binds the node flight to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public FlightWithRoutePattern bindFlight(final Flight object) {
		parameters.put("flight", Objects.requireNonNull(object, "flight must not be null!"));
		return this;
	}

	@Override
	public String toString() {
		String s = "pattern " + patternName + " {" + System.lineSeparator();
		s += "	route --> " + parameters.get("route") + System.lineSeparator();
		s += "	flight --> " + parameters.get("flight") + System.lineSeparator();
		s += "}";
		return s;
	}
}
