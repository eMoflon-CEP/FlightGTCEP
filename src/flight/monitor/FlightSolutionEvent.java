package flight.monitor;

import org.emoflon.ibex.gt.api.GraphTransformationMatch;

import com.google.common.base.Objects;

public class FlightSolutionEvent<I extends GraphTransformationMatch<?,?>, S extends GraphTransformationMatch<?,?>> extends FlightIssueEvent<I> {
	
	public final S solutionMatch;
	
	public FlightSolutionEvent(final FlightIssueEvent<I> issueEvent, final S solutionMatch, final String description) {
		super(issueEvent.issueMatch, issueEvent.description+"\n"+description);
		this.solutionMatch = solutionMatch;
	}

	public FlightSolutionEvent(final I issueMatch, final S solutionMatch, final String description) {
		super(issueMatch, description);
		this.solutionMatch = solutionMatch;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(issueMatch, solutionMatch);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof FlightSolutionEvent<?,?>))
			return false;
		
		FlightSolutionEvent<?,?> other = (FlightSolutionEvent<?,?>) obj;
		return issueMatch.equals(other.issueMatch) && solutionMatch.equals(other.solutionMatch);
	}
	
	@Override
	public String toString() {
		return description;
	}

}
