package flight.monitor;

import org.emoflon.ibex.gt.api.GraphTransformationMatch;

public class FlightIssueEvent<T extends GraphTransformationMatch<?,?>>  {
	public final T issueMatch;
	public final String description;
	
	public FlightIssueEvent(final T issueMatch, final String description) {
		this.issueMatch = issueMatch;
		this.description = description;
	}
}
