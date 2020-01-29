package FlightGTCEP.api.rules;

import FlightGTCEP.api.FlightGTCEPAPI;
import FlightGTCEP.api.matches.ReplacmentConnectingFlightMatch;
import Flights.Airport;
import Flights.Flight;
import Flights.Gate;
import Flights.Route;
import Flights.Travel;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.emoflon.ibex.common.operational.IMatch;
import org.emoflon.ibex.gt.api.GraphTransformationPattern;
import org.emoflon.ibex.gt.engine.GraphTransformationInterpreter;

/**
 * The pattern <code>replacmentConnectingFlight(travelID, oldFlightID)</code> which does the following:
 * If this pattern is not self-explaining, you really should add some comment in the specification.
 */
public class ReplacmentConnectingFlightPattern extends GraphTransformationPattern<ReplacmentConnectingFlightMatch, ReplacmentConnectingFlightPattern> {
	private static String patternName = "replacmentConnectingFlight";

	/**
	 * Creates a new pattern replacmentConnectingFlight(travelID, oldFlightID).
	 * 
	 * @param api
	 *            the API the pattern belongs to
	 * @param interpreter
	 *            the interpreter
	 * @param travelIDValue
	 *            the value for the parameter travelID
	 * @param oldFlightIDValue
	 *            the value for the parameter oldFlightID
	 */
	public ReplacmentConnectingFlightPattern(final FlightGTCEPAPI api, final GraphTransformationInterpreter interpreter,
			final java.lang.String travelIDValue, final java.lang.String oldFlightIDValue) {
		super(api, interpreter, patternName);
		setTravelID(travelIDValue);
		setOldFlightID(oldFlightIDValue);
	}

	@Override
	protected ReplacmentConnectingFlightMatch convertMatch(final IMatch match) {
		return new ReplacmentConnectingFlightMatch(this, match);
	}

	@Override
	protected List<String> getParameterNames() {
		List<String> names = new ArrayList<String>();
		names.add("arrivalGate");
		names.add("conectingFlight");
		names.add("conectingRoute");
		names.add("departingGate");
		names.add("flight");
		names.add("replacementFlight");
		names.add("replacementRoute");
		names.add("route");
		names.add("targetAirport");
		names.add("transitAirport");
		names.add("travel");
		return names;
	}

	/**
	 * Binds the node arrivalGate to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public ReplacmentConnectingFlightPattern bindArrivalGate(final Gate object) {
		parameters.put("arrivalGate", Objects.requireNonNull(object, "arrivalGate must not be null!"));
		return this;
	}

	/**
	 * Binds the node conectingFlight to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public ReplacmentConnectingFlightPattern bindConectingFlight(final Flight object) {
		parameters.put("conectingFlight", Objects.requireNonNull(object, "conectingFlight must not be null!"));
		return this;
	}

	/**
	 * Binds the node conectingRoute to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public ReplacmentConnectingFlightPattern bindConectingRoute(final Route object) {
		parameters.put("conectingRoute", Objects.requireNonNull(object, "conectingRoute must not be null!"));
		return this;
	}

	/**
	 * Binds the node departingGate to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public ReplacmentConnectingFlightPattern bindDepartingGate(final Gate object) {
		parameters.put("departingGate", Objects.requireNonNull(object, "departingGate must not be null!"));
		return this;
	}

	/**
	 * Binds the node flight to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public ReplacmentConnectingFlightPattern bindFlight(final Flight object) {
		parameters.put("flight", Objects.requireNonNull(object, "flight must not be null!"));
		return this;
	}

	/**
	 * Binds the node replacementFlight to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public ReplacmentConnectingFlightPattern bindReplacementFlight(final Flight object) {
		parameters.put("replacementFlight", Objects.requireNonNull(object, "replacementFlight must not be null!"));
		return this;
	}

	/**
	 * Binds the node replacementRoute to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public ReplacmentConnectingFlightPattern bindReplacementRoute(final Route object) {
		parameters.put("replacementRoute", Objects.requireNonNull(object, "replacementRoute must not be null!"));
		return this;
	}

	/**
	 * Binds the node route to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public ReplacmentConnectingFlightPattern bindRoute(final Route object) {
		parameters.put("route", Objects.requireNonNull(object, "route must not be null!"));
		return this;
	}

	/**
	 * Binds the node targetAirport to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public ReplacmentConnectingFlightPattern bindTargetAirport(final Airport object) {
		parameters.put("targetAirport", Objects.requireNonNull(object, "targetAirport must not be null!"));
		return this;
	}

	/**
	 * Binds the node transitAirport to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public ReplacmentConnectingFlightPattern bindTransitAirport(final Airport object) {
		parameters.put("transitAirport", Objects.requireNonNull(object, "transitAirport must not be null!"));
		return this;
	}

	/**
	 * Binds the node travel to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public ReplacmentConnectingFlightPattern bindTravel(final Travel object) {
		parameters.put("travel", Objects.requireNonNull(object, "travel must not be null!"));
		return this;
	}

	/**
	 * Sets the parameter travelID to the given value.
	 *
	 * @param value
	 *            the value to set
	 */
	public ReplacmentConnectingFlightPattern setTravelID(final java.lang.String value) {
		parameters.put("travelID", Objects.requireNonNull(value, "travelID must not be null!"));
		return this;
	}

	/**
	 * Sets the parameter oldFlightID to the given value.
	 *
	 * @param value
	 *            the value to set
	 */
	public ReplacmentConnectingFlightPattern setOldFlightID(final java.lang.String value) {
		parameters.put("oldFlightID", Objects.requireNonNull(value, "oldFlightID must not be null!"));
		return this;
	}

	@Override
	public String toString() {
		String s = "pattern " + patternName + " {" + System.lineSeparator();
		s += "	arrivalGate --> " + parameters.get("arrivalGate") + System.lineSeparator();
		s += "	conectingFlight --> " + parameters.get("conectingFlight") + System.lineSeparator();
		s += "	conectingRoute --> " + parameters.get("conectingRoute") + System.lineSeparator();
		s += "	departingGate --> " + parameters.get("departingGate") + System.lineSeparator();
		s += "	flight --> " + parameters.get("flight") + System.lineSeparator();
		s += "	replacementFlight --> " + parameters.get("replacementFlight") + System.lineSeparator();
		s += "	replacementRoute --> " + parameters.get("replacementRoute") + System.lineSeparator();
		s += "	route --> " + parameters.get("route") + System.lineSeparator();
		s += "	targetAirport --> " + parameters.get("targetAirport") + System.lineSeparator();
		s += "	transitAirport --> " + parameters.get("transitAirport") + System.lineSeparator();
		s += "	travel --> " + parameters.get("travel") + System.lineSeparator();
		s += "	travelID --> " + parameters.get("travelID") + System.lineSeparator();
		s += "	oldFlightID --> " + parameters.get("oldFlightID") + System.lineSeparator();
		s += "}";
		return s;
	}
}
