package flight.monitor;

import org.emoflon.ibex.gt.api.GraphTransformationMatch;

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

}
