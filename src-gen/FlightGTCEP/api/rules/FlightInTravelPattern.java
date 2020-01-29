package FlightGTCEP.api.rules;

import FlightGTCEP.api.FlightGTCEPAPI;
import FlightGTCEP.api.matches.FlightInTravelMatch;
import Flights.Flight;
import Flights.Travel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.emoflon.ibex.common.operational.IMatch;
import org.emoflon.ibex.gt.api.GraphTransformationPattern;
import org.emoflon.ibex.gt.engine.GraphTransformationInterpreter;

/**
 * The pattern <code>flightInTravel()</code> which does the following:
 * find travels in flight -> remaining capacity
 */
public class FlightInTravelPattern extends GraphTransformationPattern<FlightInTravelMatch, FlightInTravelPattern> {
	private static String patternName = "flightInTravel";

	/**
	 * Creates a new pattern flightInTravel().
	 * 
	 * @param api
	 *            the API the pattern belongs to
	 * @param interpreter
	 *            the interpreter
	 */
	public FlightInTravelPattern(final FlightGTCEPAPI api, final GraphTransformationInterpreter interpreter) {
		super(api, interpreter, patternName);
	}

	@Override
	protected FlightInTravelMatch convertMatch(final IMatch match) {
		return new FlightInTravelMatch(this, match);
	}

	@Override
	protected List<String> getParameterNames() {
		List<String> names = new ArrayList<String>();
		names.add("travel");
		names.add("flight");
		return names;
	}

	/**
	 * Binds the node travel to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public FlightInTravelPattern bindTravel(final Travel object) {
		parameters.put("travel", Objects.requireNonNull(object, "travel must not be null!"));
		return this;
	}

	/**
	 * Binds the node flight to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public FlightInTravelPattern bindFlight(final Flight object) {
		parameters.put("flight", Objects.requireNonNull(object, "flight must not be null!"));
		return this;
	}

	@Override
	public String toString() {
		String s = "pattern " + patternName + " {" + System.lineSeparator();
		s += "	travel --> " + parameters.get("travel") + System.lineSeparator();
		s += "	flight --> " + parameters.get("flight") + System.lineSeparator();
		s += "}";
		return s;
	}
}
