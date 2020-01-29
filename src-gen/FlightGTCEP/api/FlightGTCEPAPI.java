package FlightGTCEP.api;

import FlightGTCEP.api.rules.ExamplePatternPattern;
import FlightGTCEP.api.rules.FlightInTravelPattern;
import FlightGTCEP.api.rules.FlightWithRoutePattern;
import FlightGTCEP.api.rules.ReplacmentConnectingFlightPattern;
import FlightGTCEP.api.rules.TravelHasConnectingFlightPattern;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emoflon.ibex.common.operational.IContextPatternInterpreter;
import org.emoflon.ibex.gt.api.GraphTransformationAPI;

/**
 * The FlightGTCEPAPI with 5 rules.
 */
public class FlightGTCEPAPI extends GraphTransformationAPI {
	public static String patternPath = "FlightGTCEP/src-gen/FlightGTCEP/api/ibex-patterns.xmi";

	/**
	 * Creates a new FlightGTCEPAPI.
	 *
	 * @param engine
	 *            the engine to use for queries and transformations
	 * @param model
	 *            the resource set containing the model file
	 * @param workspacePath
	 *            the path to the workspace which is concatenated with the project
	 *            relative path to the patterns
	 */
	public FlightGTCEPAPI(final IContextPatternInterpreter engine, final ResourceSet model, final String workspacePath) {
		super(engine, model);
		URI uri = URI.createFileURI(workspacePath + patternPath);
		interpreter.loadPatternSet(uri);
	}

	/**
	 * Creates a new FlightGTCEPAPI.
	 *
	 * @param engine
	 *            the engine to use for queries and transformations
	 * @param model
	 *            the resource set containing the model file
	 * @param defaultResource
	 *            the default resource
	 * @param workspacePath
	 *            the path to the workspace which is concatenated with the project
	 *            relative path to the patterns
	 */
	public FlightGTCEPAPI(final IContextPatternInterpreter engine, final ResourceSet model, final Resource defaultResource,
			final String workspacePath) {
		super(engine, model, defaultResource);
		URI uri = URI.createFileURI(workspacePath + patternPath);
		interpreter.loadPatternSet(uri);
	}

	/**
	 * Creates a new instance of the pattern <code>examplePattern()</code> which does the following:
	 * If this pattern is not self-explaining, you really should add some comment in the specification.
	 *
	 * @return the new instance of the pattern
	 */
	public ExamplePatternPattern examplePattern() {
		return new ExamplePatternPattern(this, interpreter);
	}

	/**
	 * Creates a new instance of the pattern <code>flightInTravel()</code> which does the following:
	 * find travels in flight -> remaining capacity
	 *
	 * @return the new instance of the pattern
	 */
	public FlightInTravelPattern flightInTravel() {
		return new FlightInTravelPattern(this, interpreter);
	}

	/**
	 * Creates a new instance of the pattern <code>flightWithRoute()</code> which does the following:
	 * track new/updated flights in a pattern for Apama to filter delayed flights
	 *
	 * @return the new instance of the pattern
	 */
	public FlightWithRoutePattern flightWithRoute() {
		return new FlightWithRoutePattern(this, interpreter);
	}

	/**
	 * Creates a new instance of the pattern <code>replacmentConnectingFlight(travelID, oldFlightID)</code> which does the following:
	 * If this pattern is not self-explaining, you really should add some comment in the specification.
	 *
	 * @return the new instance of the pattern
	 */
	public ReplacmentConnectingFlightPattern replacmentConnectingFlight(final java.lang.String travelIDValue, final java.lang.String oldFlightIDValue) {
		return new ReplacmentConnectingFlightPattern(this, interpreter, travelIDValue, oldFlightIDValue);
	}

	/**
	 * Creates a new instance of the pattern <code>travelHasConnectingFlight()</code> which does the following:
	 * find travels with connecting flights
	 *
	 * @return the new instance of the pattern
	 */
	public TravelHasConnectingFlightPattern travelHasConnectingFlight() {
		return new TravelHasConnectingFlightPattern(this, interpreter);
	}
}
