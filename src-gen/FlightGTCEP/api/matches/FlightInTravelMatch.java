package FlightGTCEP.api.matches;

import FlightGTCEP.api.rules.FlightInTravelPattern;
import Flights.Flight;
import Flights.Travel;
import org.emoflon.ibex.common.operational.IMatch;
import org.emoflon.ibex.gt.api.GraphTransformationMatch;

/**
 * A match for the pattern <code>flightInTravel()</code>.
 */
public class FlightInTravelMatch extends GraphTransformationMatch<FlightInTravelMatch, FlightInTravelPattern> {
	private Travel varTravel;
	private Flight varFlight;

	/**
	 * Creates a new match for the pattern <code>flightInTravel()</code>.
	 * 
	 * @param pattern
	 *            the pattern
	 * @param match
	 *            the untyped match
	 */
	public FlightInTravelMatch(final FlightInTravelPattern pattern, final IMatch match) {
		super(pattern, match);
		varTravel = (Travel) match.get("travel");
		varFlight = (Flight) match.get("flight");
	}

	/**
	 * Returns the travel.
	 *
	 * @return the travel
	 */
	public Travel getTravel() {
		return varTravel;
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
		s += "	travel --> " + varTravel + System.lineSeparator();
		s += "	flight --> " + varFlight + System.lineSeparator();
		s += "} for " + getPattern();
		return s;
	}
}
