package FlightGTCEP.hipe.engine;

import org.eclipse.emf.common.notify.Notification;


import java.lang.Thread;
import java.time.Duration;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import static akka.pattern.Patterns.ask;

import FlightGTCEP.hipe.engine.actor.NotificationActor;
import FlightGTCEP.hipe.engine.actor.DispatchActor;
import FlightGTCEP.hipe.engine.actor.node.Airport_object;
import FlightGTCEP.hipe.engine.actor.edge.Flight_travels_0_reference;
import FlightGTCEP.hipe.engine.actor.node.Travel_object;
import FlightGTCEP.hipe.engine.actor.edge.Flight_route_0_reference;
import FlightGTCEP.hipe.engine.actor.node.Route_object;
import FlightGTCEP.hipe.engine.actor.edge.Route_src_0_reference;
import FlightGTCEP.hipe.engine.actor.edge.Route_trg_0_reference;
import FlightGTCEP.hipe.engine.actor.junction.replacmentConnectingFlight_10_junction;
import FlightGTCEP.hipe.engine.actor.junction.replacmentConnectingFlight_16_junction;
import FlightGTCEP.hipe.engine.actor.junction.replacmentConnectingFlight_9_junction;
import FlightGTCEP.hipe.engine.actor.edge.Flight_trg_0_reference;
import FlightGTCEP.hipe.engine.actor.node.Gate_object;
import FlightGTCEP.hipe.engine.actor.edge.Travel_flights_0_reference;
import FlightGTCEP.hipe.engine.actor.junction.replacmentConnectingFlight_33_junction;
import FlightGTCEP.hipe.engine.actor.edge.Flight_src_0_reference;
import FlightGTCEP.hipe.engine.actor.junction.replacmentConnectingFlight_23_junction;
import FlightGTCEP.hipe.engine.actor.junction.replacmentConnectingFlight_8_junction;
import FlightGTCEP.hipe.engine.actor.junction.travelHasConnectingFlight_61_junction;
import FlightGTCEP.hipe.engine.actor.junction.travelHasConnectingFlight_42_junction;
import FlightGTCEP.hipe.engine.actor.node.Flight_object_SP0;
import FlightGTCEP.hipe.engine.actor.node.Flight_object_SP1;

import hipe.engine.IHiPEEngine;
import hipe.engine.message.InitActor;
import hipe.engine.message.InitGenActor;
import hipe.engine.message.NoMoreInput;
import hipe.engine.message.NotificationMessage;
import hipe.engine.message.ExtractData;
import hipe.engine.message.production.ProductionResult;

import hipe.engine.util.IncUtil;
import hipe.engine.util.ProductionUtil;
import hipe.generic.actor.GenericProductionActor;
import hipe.generic.actor.junction.*;

import hipe.network.*;

public class HiPEEngine implements IHiPEEngine{

	private final ActorSystem system = ActorSystem.create("HiPE-Engine");
	private ActorRef dispatcher;
	private ActorRef notificationActor;
	private Map<String, ActorRef> name2actor = new ConcurrentHashMap<>();
	private Map<String, Class<?>> classes = new ConcurrentHashMap<>();
	private Map<String, String> productionNodes2pattern = new ConcurrentHashMap<>();
	private boolean dirty = false;
	private HiPENetwork network;
	
	private IncUtil iUtil = IncUtil.getUtil();
	private ProductionUtil pUtil = ProductionUtil.getUtil();
	
	private Thread thread;
	
	public HiPEEngine(HiPENetwork network) {
		thread = Thread.currentThread();
		iUtil.registerWakeUpCall(this::wakeUp);
		
		this.network = network;
	}
	
	public boolean wakeUp() {
		thread.interrupt();
		return true;
	}
	
	public void initialize() throws InterruptedException {
		createProductionNodes();
		createJunctionNodes();
		createReferenceNodes();
		createObjectNodes();

		classes.keySet().parallelStream().forEach(name -> {
			name2actor.put(name, system.actorOf(Props.create(classes.get(name))));			
		});
		
		dispatcher = system.actorOf(
				Props.create(DispatchActor.class, () -> new DispatchActor(name2actor)),
				"DispatchActor");
		
		notificationActor = system.actorOf(Props.create(NotificationActor.class, () -> new NotificationActor(dispatcher)), "NotificationActor");
		
		name2actor.values().forEach(actor -> actor.tell(new InitActor(name2actor), notificationActor));
		network.getNetworknode().stream().filter(n -> n instanceof AbstractJunctionNode).forEach(n -> name2actor.get(n.getName()).tell(new InitGenActor(name2actor, n), notificationActor));
		network.getNetworknode().stream().filter(n -> n instanceof ProductionNode).forEach(n -> name2actor.get(n.getName()).tell(new InitGenActor(name2actor, n), notificationActor));
		}
	
	public void createProductionNodes() {
		classes.put("examplePattern_production", GenericProductionActor.class);
		productionNodes2pattern.put("examplePattern_production", "examplePattern");
		classes.put("flightInTravel_production", GenericProductionActor.class);
		productionNodes2pattern.put("flightInTravel_production", "flightInTravel");
		classes.put("flightWithRoute_production", GenericProductionActor.class);
		productionNodes2pattern.put("flightWithRoute_production", "flightWithRoute");
		classes.put("replacmentConnectingFlight_production", GenericProductionActor.class);
		productionNodes2pattern.put("replacmentConnectingFlight_production", "replacmentConnectingFlight");
		classes.put("travelHasConnectingFlight_production", GenericProductionActor.class);
		productionNodes2pattern.put("travelHasConnectingFlight_production", "travelHasConnectingFlight");
		
	}
	
	public void createJunctionNodes() {
		classes.put("replacmentConnectingFlight_10_junction", replacmentConnectingFlight_10_junction.class);
		classes.put("replacmentConnectingFlight_19_junction", GenericJunctionActor.class);
		classes.put("replacmentConnectingFlight_16_junction", replacmentConnectingFlight_16_junction.class);
		classes.put("replacmentConnectingFlight_9_junction", replacmentConnectingFlight_9_junction.class);
		classes.put("replacmentConnectingFlight_27_junction", GenericJunctionActor.class);
		classes.put("replacmentConnectingFlight_24_junction", GenericJunctionActor.class);
		classes.put("replacmentConnectingFlight_33_junction", replacmentConnectingFlight_33_junction.class);
		classes.put("replacmentConnectingFlight_38_junction", GenericJunctionActor.class);
		classes.put("replacmentConnectingFlight_32_junction", GenericJunctionActor.class);
		classes.put("replacmentConnectingFlight_23_junction", replacmentConnectingFlight_23_junction.class);
		classes.put("replacmentConnectingFlight_8_junction", replacmentConnectingFlight_8_junction.class);
		classes.put("travelHasConnectingFlight_47_junction", GenericJunctionActor.class);
		classes.put("travelHasConnectingFlight_43_junction", GenericJunctionActor.class);
		classes.put("travelHasConnectingFlight_53_junction", GenericJunctionActor.class);
		classes.put("travelHasConnectingFlight_61_junction", travelHasConnectingFlight_61_junction.class);
		classes.put("travelHasConnectingFlight_58_junction", GenericJunctionActor.class);
		classes.put("travelHasConnectingFlight_52_junction", GenericJunctionActor.class);
		classes.put("travelHasConnectingFlight_42_junction", travelHasConnectingFlight_42_junction.class);
	}
	
	public void createReferenceNodes() {
		classes.put("Flight_travels_0_reference",Flight_travels_0_reference.class);
		classes.put("Flight_route_0_reference",Flight_route_0_reference.class);
		classes.put("Route_src_0_reference",Route_src_0_reference.class);
		classes.put("Route_trg_0_reference",Route_trg_0_reference.class);
		classes.put("Flight_trg_0_reference",Flight_trg_0_reference.class);
		classes.put("Travel_flights_0_reference",Travel_flights_0_reference.class);
		classes.put("Flight_src_0_reference",Flight_src_0_reference.class);
		
	}
	
	public void createObjectNodes() {
		classes.put("Airport_object",Airport_object.class);
		classes.put("Travel_object",Travel_object.class);
		classes.put("Route_object",Route_object.class);
		classes.put("Gate_object",Gate_object.class);
		classes.put("Flight_object_SP0",Flight_object_SP0.class);
		classes.put("Flight_object_SP1",Flight_object_SP1.class);
		
	}

	/**
	 * delegate notifications to dispatcher actor
	 * @param notification
	 */			
	public void handleNotification(Notification notification) {
		try {
			dirty = true;
			ask(notificationActor, new NotificationMessage(notification), Duration.ofHours(24)).toCompletableFuture().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}			
	}
	
	public Map<String, ProductionResult> extractData() throws InterruptedException {
		if(!dirty) {
			return java.util.Collections.synchronizedMap(new HashMap<>());
		}	
		dirty = false;
		
		iUtil.clean();
		pUtil.clean();
			
		notificationActor.tell(new NoMoreInput(), notificationActor);
		
		try {
			Thread.sleep(100000000);
		} catch(Exception e) {
		}
		
		return pUtil.getProductionResults();
	}
	
	public void terminate() {
		system.terminate();	
	}
}

