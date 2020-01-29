package FlightGTCEP.api.matches;

import FlightGTCEP.api.rules.ExamplePatternPattern;
import Flights.Airport;
import org.emoflon.ibex.common.operational.IMatch;
import org.emoflon.ibex.gt.api.GraphTransformationMatch;

/**
 * A match for the pattern <code>examplePattern()</code>.
 */
public class ExamplePatternMatch extends GraphTransformationMatch<ExamplePatternMatch, ExamplePatternPattern> {
	private Airport varObject;

	/**
	 * Creates a new match for the pattern <code>examplePattern()</code>.
	 * 
	 * @param pattern
	 *            the pattern
	 * @param match
	 *            the untyped match
	 */
	public ExamplePatternMatch(final ExamplePatternPattern pattern, final IMatch match) {
		super(pattern, match);
		varObject = (Airport) match.get("object");
	}

	/**
	 * Returns the object.
	 *
	 * @return the object
	 */
	public Airport getObject() {
		return varObject;
	}

	@Override
	public String toString() {
		String s = "match {" + System.lineSeparator();
		s += "	object --> " + varObject + System.lineSeparator();
		s += "} for " + getPattern();
		return s;
	}
}
