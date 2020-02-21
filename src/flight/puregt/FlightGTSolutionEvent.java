package flight.puregt;

import org.emoflon.ibex.gt.api.GraphTransformationMatch;

public class FlightGTSolutionEvent<I extends GraphTransformationMatch<?,?>, S extends GraphTransformationMatch<?,?>> extends FlightGTIssueEvent<I> {
	
	public final S solutionMatch;
	
	public FlightGTSolutionEvent(final FlightGTIssueEvent<I> issueEvent, final S solutionMatch, final String description) {
		super(issueEvent.issueMatch, issueEvent.description+"\n"+description);
		this.solutionMatch = solutionMatch;
	}

	public FlightGTSolutionEvent(final I issueMatch, final S solutionMatch, final String description) {
		super(issueMatch, description);
		this.solutionMatch = solutionMatch;
	}

}
