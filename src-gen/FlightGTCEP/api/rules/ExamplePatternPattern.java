package FlightGTCEP.api.rules;

import FlightGTCEP.api.FlightGTCEPAPI;
import FlightGTCEP.api.matches.ExamplePatternMatch;
import Flights.Airport;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.emoflon.ibex.common.operational.IMatch;
import org.emoflon.ibex.gt.api.GraphTransformationPattern;
import org.emoflon.ibex.gt.engine.GraphTransformationInterpreter;

/**
 * The pattern <code>examplePattern()</code> which does the following:
 * If this pattern is not self-explaining, you really should add some comment in the specification.
 */
public class ExamplePatternPattern extends GraphTransformationPattern<ExamplePatternMatch, ExamplePatternPattern> {
	private static String patternName = "examplePattern";

	/**
	 * Creates a new pattern examplePattern().
	 * 
	 * @param api
	 *            the API the pattern belongs to
	 * @param interpreter
	 *            the interpreter
	 */
	public ExamplePatternPattern(final FlightGTCEPAPI api, final GraphTransformationInterpreter interpreter) {
		super(api, interpreter, patternName);
	}

	@Override
	protected ExamplePatternMatch convertMatch(final IMatch match) {
		return new ExamplePatternMatch(this, match);
	}

	@Override
	protected List<String> getParameterNames() {
		List<String> names = new ArrayList<String>();
		names.add("object");
		return names;
	}

	/**
	 * Binds the node object to the given object.
	 *
	 * @param object
	 *            the object to set
	 */
	public ExamplePatternPattern bindObject(final Airport object) {
		parameters.put("object", Objects.requireNonNull(object, "object must not be null!"));
		return this;
	}

	@Override
	public String toString() {
		String s = "pattern " + patternName + " {" + System.lineSeparator();
		s += "	object --> " + parameters.get("object") + System.lineSeparator();
		s += "}";
		return s;
	}
}
