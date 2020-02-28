package flight.monitor;

import org.emoflon.ibex.gt.api.GraphTransformationMatch;

public class FlightIssueEvent<T extends GraphTransformationMatch<?,?>>  {
	public final T issueMatch;
	public final String description;
	
	public FlightIssueEvent(final T issueMatch, final String description) {
		this.issueMatch = issueMatch;
		this.description = description;
	}
	
	@Override
	public int hashCode() {
		return issueMatch.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof FlightIssueEvent<?>))
			return false;
		
		FlightIssueEvent<?> other = (FlightIssueEvent<?>) obj;
		return issueMatch.equals(other.issueMatch);
	}
	
	@Override
	public String toString() {
		return description;
	}
}
