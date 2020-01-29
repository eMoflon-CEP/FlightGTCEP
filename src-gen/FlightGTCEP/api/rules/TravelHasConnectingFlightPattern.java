package FlightGTCEP.api.rules;

import FlightGTCEP.api.FlightGTCEPAPI;
import FlightGTCEP.api.matches.TravelHasConnectingFlightMatch;
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
 * The pattern <code>travelHasConnectingFlight()</code> which does the following:
 * find travels with connecting flights
 */
public class TravelHasConnectingFlightPattern extends GraphTransformationPattern<TravelHasConnectingFlightMatch, TravelHasConnectingFlightPattern> {
	private static String patternName = "travelHasConnectingFlight";

	/**
	 * Creates a new pattern travelHasConnectingFlight().
	 * 
	 * @param api
	 *            the API the pattern belongs to
	 * @param interpreter
	 *            the interpreter
	 */
	public TravelHasConnectingFlightPattern(final FlightGTCEPAPI api, final GraphTransformationInterpreter interpreter) {
		super(api, interpreter, patternName);
	}

	@Override
	protected TravelHasConnectingFlightMatch convertMatch(final IMatch match) {
		return new TravelHasConnectingFlightMatch(this, match);
	}

	@Override
	protected List<String> getParameterNames() {
		List<String> names = new ArrayList<String>();
		names.add("transitAirport");
		names.add("arrivalGate");
		names.add("departingGate");
		names.add("route");
		names.add("flight");
		names.add("conectingRoute");
		names.add("conectingFlight");
		names.add("travel");
		return names;
	}

	/**
	 * Binds the node transitAirport to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public TravelHasConnectingFlightPattern bindTransitAirport(final Airport object) {
		parameters.put("transitAirport", Objects.requireNonNull(object, "transitAirport must not be null!"));
		return this;
	}

	/**
	 * Binds the node arrivalGate to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public TravelHasConnectingFlightPattern bindArrivalGate(final Gate object) {
		parameters.put("arrivalGate", Objects.requireNonNull(object, "arrivalGate must not be null!"));
		return this;
	}

	/**
	 * Binds the node departingGate to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public TravelHasConnectingFlightPattern bindDepartingGate(final Gate object) {
		parameters.put("departingGate", Objects.requireNonNull(object, "departingGate must not be null!"));
		return this;
	}

	/**
	 * Binds the node route to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public TravelHasConnectingFlightPattern bindRoute(final Route object) {
		parameters.put("route", Objects.requireNonNull(object, "route must not be null!"));
		return this;
	}

	/**
	 * Binds the node flight to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public TravelHasConnectingFlightPattern bindFlight(final Flight object) {
		parameters.put("flight", Objects.requireNonNull(object, "flight must not be null!"));
		return this;
	}

	/**
	 * Binds the node conectingRoute to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public TravelHasConnectingFlightPattern bindConectingRoute(final Route object) {
		parameters.put("conectingRoute", Objects.requireNonNull(object, "conectingRoute must not be null!"));
		return this;
	}

	/**
	 * Binds the node conectingFlight to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public TravelHasConnectingFlightPattern bindConectingFlight(final Flight object) {
		parameters.put("conectingFlight", Objects.requireNonNull(object, "conectingFlight must not be null!"));
		return this;
	}

	/**
	 * Binds the node travel to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public TravelHasConnectingFlightPattern bindTravel(final Travel object) {
		parameters.put("travel", Objects.requireNonNull(object, "travel must not be null!"));
		return this;
	}

	@Override
	public String toString() {
		String s = "pattern " + patternName + " {" + System.lineSeparator();
		s += "	transitAirport --> " + parameters.get("transitAirport") + System.lineSeparator();
		s += "	arrivalGate --> " + parameters.get("arrivalGate") + System.lineSeparator();
		s += "	departingGate --> " + parameters.get("departingGate") + System.lineSeparator();
		s += "	route --> " + parameters.get("route") + System.lineSeparator();
		s += "	flight --> " + parameters.get("flight") + System.lineSeparator();
		s += "	conectingRoute --> " + parameters.get("conectingRoute") + System.lineSeparator();
		s += "	conectingFlight --> " + parameters.get("conectingFlight") + System.lineSeparator();
		s += "	travel --> " + parameters.get("travel") + System.lineSeparator();
		s += "}";
		return s;
	}
}
