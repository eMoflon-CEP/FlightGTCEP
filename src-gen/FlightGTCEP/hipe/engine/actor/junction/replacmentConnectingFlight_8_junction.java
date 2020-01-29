package FlightGTCEP.hipe.engine.actor.junction;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java.util.Set;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;

import hipe.engine.actor.Port;
import hipe.engine.util.HiPESet;
import hipe.engine.match.EdgeMatch;
import hipe.engine.match.HMatch;
import hipe.engine.actor.junction.PortJunction;
import hipe.engine.actor.junction.PortJunctionLeft;
import hipe.engine.actor.junction.PortJunctionRight;
import hipe.engine.message.input.AttributeChanged;

import hipe.generic.actor.junction.GenericJunctionActor;

import hipe.network.AbstractJunctionNode;

public class replacmentConnectingFlight_8_junction extends GenericJunctionActor{
	private Map<Object, Collection<HMatch>> flightAttrMap = new HashMap<>();
	
	@Override
	protected void initializePorts(Map<String, ActorRef> name2actor, AbstractJunctionNode node) {
		ports = new LinkedList<>();
		ports.add(new PortJunction(getSelf(), name2actor.get("replacmentConnectingFlight_production"), this::check_constraint_6));
	}
	
	@Override
	protected void registerMatchForAttributeChanges(HMatch match) {
		Object[] matchObjects = match.getNodes();
		Collection<HMatch> flight_4_Matches = flightAttrMap.get(matchObjects[4]);
		if(flight_4_Matches == null) {
			flight_4_Matches = new LinkedList<>();
			flightAttrMap.put(matchObjects[4], flight_4_Matches);
		}
		
		flight_4_Matches.add(match);
		
		Collection<HMatch> flight_6_Matches = flightAttrMap.get(matchObjects[6]);
		if(flight_6_Matches == null) {
			flight_6_Matches = new LinkedList<>();
			flightAttrMap.put(matchObjects[6], flight_6_Matches);
		}
		
		flight_6_Matches.add(match);
		
		Collection<HMatch> flight_9_Matches = flightAttrMap.get(matchObjects[9]);
		if(flight_9_Matches == null) {
			flight_9_Matches = new LinkedList<>();
			flightAttrMap.put(matchObjects[9], flight_9_Matches);
		}
		
		flight_9_Matches.add(match);
		
	}
	
	@Override
	protected void deregisterMatchForAttributeChanges(Set<HMatch> matches, HMatch match) {
		Object[] matchObjects = match.getNodes();
		Collection<HMatch> matches_0 = flightAttrMap.get(matchObjects[4]);
		if(matches_0 != null) {
			matches.remove(match);
		}
		Collection<HMatch> matches_1 = flightAttrMap.get(matchObjects[6]);
		if(matches_1 != null) {
			matches.remove(match);
		}
		Collection<HMatch> matches_2 = flightAttrMap.get(matchObjects[9]);
		if(matches_2 != null) {
			matches.remove(match);
		}
	}
	
	@Override
	protected void changeAttribute(AttributeChanged<HMatch> message) {
		for(Port<?> port : ports) {
			message.initialMessage.increment();
			port.forwardMessage(message);
		}
		Object obj = message.node;
		if(obj instanceof Flights.Flight) {
			if(flightAttrMap.containsKey(obj)) {
				for(HMatch match : flightAttrMap.get(obj)) {
					for(Port<HMatch> port : ports) {
						port.sendAttributeChanged(message.initialMessage, match);
					}
				}
			}
		}
		
		message.initialMessage.decrement();
	}
	
	public boolean check_constraint_6(HMatch match) {
		Flights.Flight replacementFlight = (Flights.Flight) match.getNodes()[4];
		Flights.Route route = (Flights.Route) match.getNodes()[5];
		Flights.Route replacementRoute = (Flights.Route) match.getNodes()[3];
		Flights.Flight conectingFlight = (Flights.Flight) match.getNodes()[9];
		Flights.Flight flight = (Flights.Flight) match.getNodes()[6];
		boolean predicate = !conectingFlight.equals(replacementFlight) && !flight.equals(replacementFlight) && !replacementRoute.equals(route) && replacementFlight.getDeparture()>conectingFlight.getDeparture();
		match.setConstraintSatisfied(predicate);
		return predicate;
	}
	
}

