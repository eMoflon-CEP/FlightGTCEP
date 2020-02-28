package flight.monitor;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.emoflon.ibex.gt.api.GraphTransformationMatch;

import static org.emoflon.flight.model.util.LongDateHelper.getTimeInMs;

import FlightGTCEP.api.FlightGTCEPAPI;
import FlightGTCEP.api.FlightGTCEPApp;
import FlightGTCEP.api.FlightGTCEPHiPEApp;
import FlightGTCEP.api.matches.ConnectingFlightAlternativeMatch;
import FlightGTCEP.api.matches.TravelHasConnectingFlightMatch;
import Flights.Airport;
import Flights.FlightModel;
import Flights.Gate;

public abstract class FlightMonitor {
	
	protected FlightGTCEPApp app;
	protected FlightGTCEPAPI api;
	
	protected LinkedBlockingQueue<String> issueMessages;
	protected LinkedBlockingQueue<String> infoMessages;
	protected LinkedBlockingQueue<String> solutionMessages;
	@SuppressWarnings("rawtypes")
	protected Map<GraphTransformationMatch, FlightIssueEvent<GraphTransformationMatch>> issues;
	@SuppressWarnings("rawtypes")
	protected Map<FlightIssueEvent<GraphTransformationMatch>, FlightSolutionEvent<GraphTransformationMatch, GraphTransformationMatch>> solutions;
	
	protected FlightMonitor() {
		issues = Collections.synchronizedMap(new LinkedHashMap<>());
		solutions = Collections.synchronizedMap(new LinkedHashMap<>());
		issueMessages = new LinkedBlockingQueue<>();
		infoMessages = new LinkedBlockingQueue<>();
		solutionMessages = new LinkedBlockingQueue<>();
	}
	
	public void initModelAndEngine(String modelPath) {
		app = new FlightGTCEPHiPEApp();
		app.registerMetaModels();
		URI uri = URI.createFileURI(modelPath);
		app.loadModel(uri);
		api = app.initAPI();
	}
	
	public void initModelAndEngine(FlightModel model) {
		app = new FlightGTCEPHiPEApp();
		app.registerMetaModels();
		app.createModel(URI.createFileURI(model.toString()));
		app.getModel().getResources().get(0).getContents().add(model);
		api = app.initAPI();
	}
	
	public FlightGTCEPApp getApp() {
		return app;
	}
	
	public FlightGTCEPAPI getApi() {
		return api;
	}
	
	public FlightModel getModel() {
		return (FlightModel) app.getModel().getResources().get(0).getContents().get(0);
	}
	
	public abstract void initMatchSubscribers();

	public abstract void update(boolean debug);
	
	public abstract Collection<TravelHasConnectingFlightMatch> getWorkingConnectingFlightTravels();
	public abstract Collection<TravelHasConnectingFlightMatch> getDelayedConnectingFlightTravels();
	
	public abstract void shutdown();
	
	@SuppressWarnings("rawtypes")
	public synchronized Set<FlightIssueEvent<GraphTransformationMatch>> getIssues() {
		return issues.values().stream().collect(Collectors.toSet());
	}
	
	@SuppressWarnings("rawtypes")
	public synchronized Set<FlightSolutionEvent<GraphTransformationMatch, GraphTransformationMatch>> getSolutions() {
		return solutions.values().stream().collect(Collectors.toSet());
	}
	
	
	public synchronized static long calcGateDistance(final Airport airport, final Gate arrival, final Gate departure) {
//		return (long)(airport.getSize() * getTimeInMs(Math.abs(arrival.getPosition()-departure.getPosition())));
		return (long)(0.1 * getTimeInMs(Math.abs(arrival.getPosition()-departure.getPosition())));
	}
	
	public static SortedSet<ConnectingFlightAlternativeMatch> createSortedSet() {
		Comparator<ConnectingFlightAlternativeMatch> compare = Comparator.comparing((match)->match.getReplacementFlight().getArrival().getTime());
		return new TreeSet<ConnectingFlightAlternativeMatch>(compare);
	}
	
	@SuppressWarnings("rawtypes")
	public synchronized void addIssue(final GraphTransformationMatch match, final String issue) {
		if(issues.containsKey(match))
			return;
		
		issues.put(match, new FlightIssueEvent<GraphTransformationMatch>(match, issue));
		issueMessages.add(issue);
	}
	
	@SuppressWarnings("rawtypes")
	public synchronized void removeIssue(final GraphTransformationMatch match) {
		FlightIssueEvent<GraphTransformationMatch> issue = issues.remove(match);
		if(issue != null) {
			removeSolution(issue);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public synchronized void addSolution(final GraphTransformationMatch issueMatch, final GraphTransformationMatch solutionMatch, final String solution) {
		FlightIssueEvent<GraphTransformationMatch> issue = issues.get(issueMatch);
		if(issue == null)
			return;
		if(solutions.containsKey(issue))
			return;
		
		solutions.put(issue, new FlightSolutionEvent<GraphTransformationMatch, GraphTransformationMatch>(issueMatch, solutionMatch, solution));
		solutionMessages.add(solution);
	}
	
	@SuppressWarnings("rawtypes")
	public synchronized void removeSolution(final FlightIssueEvent<GraphTransformationMatch> issue) {
		if(solutions.containsKey(issue))
			solutions.remove(issue);
	}

}
