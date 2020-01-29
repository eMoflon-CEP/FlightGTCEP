package FlightGTCEP.hipe.engine.actor;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import static akka.pattern.Patterns.ask;

import hipe.engine.util.IncUtil;
import hipe.engine.message.NewInput;
import hipe.engine.message.NoMoreInput;
import hipe.engine.message.input.InputContainer;
import hipe.engine.message.input.ObjectAdded;
import hipe.engine.message.input.ObjectDeleted;
import hipe.engine.message.input.ReferenceAdded;
import hipe.engine.message.input.ReferenceDeleted;		
import hipe.engine.message.input.AttributeChanged;

public class DispatchActor extends AbstractActor {
	
	private Map<String, ActorRef> name2actor;
	
	private Map<Object, Consumer<Object>> type2addConsumer = new HashMap<>();
	private Map<Object, Consumer<Notification>> feature2setConsumer = new HashMap<>();
	private Map<Object, Consumer<Notification>> feature2addEdgeConsumer = new HashMap<>();
	private Map<Object, Consumer<Notification>> feature2removeEdgeConsumer = new HashMap<>();
	
	private IncUtil util = IncUtil.getUtil();
	
	public DispatchActor(Map<String, ActorRef> name2actor) {
		this.name2actor = name2actor;
		
		initializeAdd();
		initializeSet();
		initializeAddEdge();
		initializeRemoveEdge();
	}
	
	private void initializeAdd() {
		type2addConsumer.put(Flights.FlightsPackage.eINSTANCE.getBookings(), obj -> {
			Flights.Bookings _bookings = (Flights.Bookings) obj;
		});
		type2addConsumer.put(Flights.FlightsPackage.eINSTANCE.getTravel(), obj -> {
			Flights.Travel _travel = (Flights.Travel) obj;
			util.newMessage();
			name2actor.get("Travel_object").tell(new ObjectAdded<Flights.Travel>(_travel), getSelf());
		});
		type2addConsumer.put(Flights.FlightsPackage.eINSTANCE.getBooking(), obj -> {
			Flights.Booking _booking = (Flights.Booking) obj;
		});
		type2addConsumer.put(Flights.FlightsPackage.eINSTANCE.getPlanes(), obj -> {
			Flights.Planes _planes = (Flights.Planes) obj;
		});
		type2addConsumer.put(Flights.FlightsPackage.eINSTANCE.getFlightModel(), obj -> {
			Flights.FlightModel _flightmodel = (Flights.FlightModel) obj;
		});
		type2addConsumer.put(Flights.FlightsPackage.eINSTANCE.getAirport(), obj -> {
			Flights.Airport _airport = (Flights.Airport) obj;
			util.newMessage();
			name2actor.get("Airport_object").tell(new ObjectAdded<Flights.Airport>(_airport), getSelf());
		});
		type2addConsumer.put(Flights.FlightsPackage.eINSTANCE.getRoutes(), obj -> {
			Flights.Routes _routes = (Flights.Routes) obj;
		});
		type2addConsumer.put(Flights.FlightsPackage.eINSTANCE.getRoute(), obj -> {
			Flights.Route _route = (Flights.Route) obj;
			util.newMessage();
			name2actor.get("Route_object").tell(new ObjectAdded<Flights.Route>(_route), getSelf());
		});
		type2addConsumer.put(Flights.FlightsPackage.eINSTANCE.getAirports(), obj -> {
			Flights.Airports _airports = (Flights.Airports) obj;
		});
		type2addConsumer.put(Flights.FlightsPackage.eINSTANCE.getFlightContainer(), obj -> {
			Flights.FlightContainer _flightcontainer = (Flights.FlightContainer) obj;
		});
		type2addConsumer.put(Flights.FlightsPackage.eINSTANCE.getPerson(), obj -> {
			Flights.Person _person = (Flights.Person) obj;
		});
		type2addConsumer.put(Flights.FlightsPackage.eINSTANCE.getPlane(), obj -> {
			Flights.Plane _plane = (Flights.Plane) obj;
		});
		type2addConsumer.put(Flights.FlightsPackage.eINSTANCE.getGate(), obj -> {
			Flights.Gate _gate = (Flights.Gate) obj;
			util.newMessage();
			name2actor.get("Gate_object").tell(new ObjectAdded<Flights.Gate>(_gate), getSelf());
		});
		type2addConsumer.put(Flights.FlightsPackage.eINSTANCE.getFlight(), obj -> {
			Flights.Flight _flight = (Flights.Flight) obj;
			util.newMessage();
			name2actor.get("Flight_object_SP0").tell(new ObjectAdded<Flights.Flight>(_flight), getSelf());
			util.newMessage();
			name2actor.get("Flight_object_SP1").tell(new ObjectAdded<Flights.Flight>(_flight), getSelf());
		});
		type2addConsumer.put(Flights.FlightsPackage.eINSTANCE.getPersons(), obj -> {
			Flights.Persons _persons = (Flights.Persons) obj;
		});
		type2addConsumer.put(Flights.FlightsPackage.eINSTANCE.getFlightObject(), obj -> {
			Flights.FlightObject _flightobject = (Flights.FlightObject) obj;
		});
	}
	
	private void initializeSet() {
		feature2setConsumer.put(Flights.FlightsPackage.eINSTANCE.getFlight_Departure(), notification -> {
			if(notification.getNotifier() instanceof Flights.Flight) {
				util.newMessage();
				name2actor.get("Flight_object_SP1").tell(new AttributeChanged<Flights.Flight>((Flights.Flight) notification.getNotifier()), getSelf());
			}
			if(notification.getNotifier() instanceof Flights.Flight) {
				util.newMessage();
				name2actor.get("Flight_object_SP0").tell(new AttributeChanged<Flights.Flight>((Flights.Flight) notification.getNotifier()), getSelf());
			}
		});
		
	}
	
	private void initializeAddEdge() {
		feature2addEdgeConsumer.put(Flights.FlightsPackage.eINSTANCE.getFlight_Travels(), notification -> {
			util.newMessage();
			name2actor.get("Flight_object_SP0").tell(new ReferenceAdded<Flights.Flight, Flights.Travel>((Flights.Flight) notification.getNotifier(), (Flights.Travel) notification.getNewValue(), name2actor.get("Flight_travels_0_reference")), getSelf());
		});
		feature2addEdgeConsumer.put(Flights.FlightsPackage.eINSTANCE.getFlight_Route(), notification -> {
			util.newMessage();
			name2actor.get("Flight_object_SP1").tell(new ReferenceAdded<Flights.Flight, Flights.Route>((Flights.Flight) notification.getNotifier(), (Flights.Route) notification.getNewValue(), name2actor.get("Flight_route_0_reference")), getSelf());
		});
		feature2addEdgeConsumer.put(Flights.FlightsPackage.eINSTANCE.getRoute_Src(), notification -> {
			util.newMessage();
			name2actor.get("Route_object").tell(new ReferenceAdded<Flights.Route, Flights.Airport>((Flights.Route) notification.getNotifier(), (Flights.Airport) notification.getNewValue(), name2actor.get("Route_src_0_reference")), getSelf());
		});
		feature2addEdgeConsumer.put(Flights.FlightsPackage.eINSTANCE.getRoute_Trg(), notification -> {
			util.newMessage();
			name2actor.get("Route_object").tell(new ReferenceAdded<Flights.Route, Flights.Airport>((Flights.Route) notification.getNotifier(), (Flights.Airport) notification.getNewValue(), name2actor.get("Route_trg_0_reference")), getSelf());
		});
		feature2addEdgeConsumer.put(Flights.FlightsPackage.eINSTANCE.getFlight_Trg(), notification -> {
			util.newMessage();
			name2actor.get("Flight_object_SP0").tell(new ReferenceAdded<Flights.Flight, Flights.Gate>((Flights.Flight) notification.getNotifier(), (Flights.Gate) notification.getNewValue(), name2actor.get("Flight_trg_0_reference")), getSelf());
		});
		feature2addEdgeConsumer.put(Flights.FlightsPackage.eINSTANCE.getTravel_Flights(), notification -> {
			util.newMessage();
			name2actor.get("Travel_object").tell(new ReferenceAdded<Flights.Travel, Flights.Flight>((Flights.Travel) notification.getNotifier(), (Flights.Flight) notification.getNewValue(), name2actor.get("Travel_flights_0_reference")), getSelf());
		});
		feature2addEdgeConsumer.put(Flights.FlightsPackage.eINSTANCE.getFlight_Src(), notification -> {
			util.newMessage();
			name2actor.get("Flight_object_SP0").tell(new ReferenceAdded<Flights.Flight, Flights.Gate>((Flights.Flight) notification.getNotifier(), (Flights.Gate) notification.getNewValue(), name2actor.get("Flight_src_0_reference")), getSelf());
		});
	}
	
	private void initializeRemoveEdge() {
		feature2removeEdgeConsumer.put(Flights.FlightsPackage.eINSTANCE.getFlight_Travels(), notification -> {
			util.newMessage();
			name2actor.get("Flight_object_SP0").tell(new ReferenceDeleted<Flights.Flight, Flights.Travel>((Flights.Flight) notification.getNotifier(), (Flights.Travel) notification.getOldValue(), name2actor.get("Flight_travels_0_reference")), getSelf());
		});
		feature2removeEdgeConsumer.put(Flights.FlightsPackage.eINSTANCE.getFlight_Route(), notification -> {
			util.newMessage();
			name2actor.get("Flight_object_SP1").tell(new ReferenceDeleted<Flights.Flight, Flights.Route>((Flights.Flight) notification.getNotifier(), (Flights.Route) notification.getOldValue(), name2actor.get("Flight_route_0_reference")), getSelf());
		});
		feature2removeEdgeConsumer.put(Flights.FlightsPackage.eINSTANCE.getRoute_Src(), notification -> {
			util.newMessage();
			name2actor.get("Route_object").tell(new ReferenceDeleted<Flights.Route, Flights.Airport>((Flights.Route) notification.getNotifier(), (Flights.Airport) notification.getOldValue(), name2actor.get("Route_src_0_reference")), getSelf());
		});
		feature2removeEdgeConsumer.put(Flights.FlightsPackage.eINSTANCE.getRoute_Trg(), notification -> {
			util.newMessage();
			name2actor.get("Route_object").tell(new ReferenceDeleted<Flights.Route, Flights.Airport>((Flights.Route) notification.getNotifier(), (Flights.Airport) notification.getOldValue(), name2actor.get("Route_trg_0_reference")), getSelf());
		});
		feature2removeEdgeConsumer.put(Flights.FlightsPackage.eINSTANCE.getFlight_Trg(), notification -> {
			util.newMessage();
			name2actor.get("Flight_object_SP0").tell(new ReferenceDeleted<Flights.Flight, Flights.Gate>((Flights.Flight) notification.getNotifier(), (Flights.Gate) notification.getOldValue(), name2actor.get("Flight_trg_0_reference")), getSelf());
		});
		feature2removeEdgeConsumer.put(Flights.FlightsPackage.eINSTANCE.getTravel_Flights(), notification -> {
			util.newMessage();
			name2actor.get("Travel_object").tell(new ReferenceDeleted<Flights.Travel, Flights.Flight>((Flights.Travel) notification.getNotifier(), (Flights.Flight) notification.getOldValue(), name2actor.get("Travel_flights_0_reference")), getSelf());
		});
		feature2removeEdgeConsumer.put(Flights.FlightsPackage.eINSTANCE.getFlight_Src(), notification -> {
			util.newMessage();
			name2actor.get("Flight_object_SP0").tell(new ReferenceDeleted<Flights.Flight, Flights.Gate>((Flights.Flight) notification.getNotifier(), (Flights.Gate) notification.getOldValue(), name2actor.get("Flight_src_0_reference")), getSelf());
		});
	}

	@Override
	public void preStart() throws Exception {
		super.preStart();
	}

	@Override
	public void postStop() throws Exception {
		super.postStop();
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder() //
				.match(Notification.class, this::handleNotification) //
				.match(NoMoreInput.class, this::sendFinished) //
				.build();
	}

	private void sendFinished(NoMoreInput m) {
		util.allMessagesInserted();
	}
	
	private void handleNotification(Notification notification) {
		switch (notification.getEventType()) {
		case Notification.ADD:
			handleAdd(notification);
			return;
		case Notification.REMOVE:
			handleRemove(notification);
			return;
		case Notification.REMOVING_ADAPTER:
			handleRemoveAdapter(notification);
			return;	
		case Notification.SET:
			handleSet(notification);
		}
	}

	private void handleAdd(Notification notification) {
		handleAddedNode(notification.getNewValue());
		handleAddedEdge(notification);
	}

	private void handleAddedNode(Object node) {
		if(node == null) 
			return;
			
		EObject obj = (EObject) node;
		if(type2addConsumer.containsKey(obj.eClass())) {
			type2addConsumer.get(obj.eClass()).accept(node);
		}
	}
	
	private void handleSet(Notification notification) {
		Object feature = notification.getFeature();
		if(feature2setConsumer.containsKey(feature)) {
			feature2setConsumer.get(feature).accept(notification);
		}
	}

	private void handleAddedEdge(Notification notification) {
		Object feature = notification.getFeature();
		if(feature2addEdgeConsumer.containsKey(feature)) {
			feature2addEdgeConsumer.get(feature).accept(notification);
		}
	}

	private void handleRemove(Notification notification) {
		Object feature = notification.getFeature();
		if(feature2removeEdgeConsumer.containsKey(feature)) {
			feature2removeEdgeConsumer.get(feature).accept(notification);
		}
	}
	
	private void handleRemoveAdapter(Notification notification) {
		Object node = notification.getNotifier();
		if (node instanceof Flights.Airport) {
			util.newMessage();
			name2actor.get("Airport_object").tell(new ObjectDeleted<Flights.Airport>((Flights.Airport) node), getSelf());
		}
		if (node instanceof Flights.Travel) {
			util.newMessage();
			name2actor.get("Travel_object").tell(new ObjectDeleted<Flights.Travel>((Flights.Travel) node), getSelf());
		}
		if (node instanceof Flights.Route) {
			util.newMessage();
			name2actor.get("Route_object").tell(new ObjectDeleted<Flights.Route>((Flights.Route) node), getSelf());
		}
		if (node instanceof Flights.Gate) {
			util.newMessage();
			name2actor.get("Gate_object").tell(new ObjectDeleted<Flights.Gate>((Flights.Gate) node), getSelf());
		}
		if (node instanceof Flights.Flight) {
			util.newMessage();
			name2actor.get("Flight_object_SP0").tell(new ObjectDeleted<Flights.Flight>((Flights.Flight) node), getSelf());
		}
		if (node instanceof Flights.Flight) {
			util.newMessage();
			name2actor.get("Flight_object_SP1").tell(new ObjectDeleted<Flights.Flight>((Flights.Flight) node), getSelf());
		}
	}
}

