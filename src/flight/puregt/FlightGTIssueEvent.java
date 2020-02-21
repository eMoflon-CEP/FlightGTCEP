package flight.puregt;

import org.emoflon.ibex.gt.api.GraphTransformationMatch;

public class FlightGTIssueEvent<T extends GraphTransformationMatch<?,?>>  {
	public final T issueMatch;
	public final String description;
	
	public FlightGTIssueEvent(final T issueMatch, final String description) {
		this.issueMatch = issueMatch;
		this.description = description;
	}
}
