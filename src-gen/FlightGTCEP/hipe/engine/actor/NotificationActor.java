package FlightGTCEP.hipe.engine.actor;

import java.util.Collections;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Function;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentAdapter;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import hipe.engine.message.NewInput;
import hipe.engine.message.NoMoreInput;
import hipe.engine.message.NotificationMessage;

public class NotificationActor extends AbstractActor {
	
	private Queue<Notification> notificationCache = new LinkedBlockingQueue<Notification>();
	private ActorRef dispatchActor;
	private Set<Object> discoveredObjects = Collections.synchronizedSet(new LinkedHashSet<>());
	
	private Map<Object, Function<EObject, Collection<EObject>>> explorationConsumer = new HashMap<>();
	
	public long time = 0;
	public long tell_time = 0;
	
	public NotificationActor(ActorRef dispatchActor) {
		this.dispatchActor = dispatchActor;
		initializeExploration();
	}
	
	private void initializeExploration() {
		explorationConsumer.put(Flights.FlightsPackage.eINSTANCE.getBookings(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			Flights.Bookings _bookings = (Flights.Bookings) obj;
			children.addAll(_bookings.getBookings());
			return children;
		});
		explorationConsumer.put(Flights.FlightsPackage.eINSTANCE.getTravel(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			return children;
		});
		explorationConsumer.put(Flights.FlightsPackage.eINSTANCE.getBooking(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			Flights.Booking _booking = (Flights.Booking) obj;
			children.addAll(_booking.getTravels());
			return children;
		});
		explorationConsumer.put(Flights.FlightsPackage.eINSTANCE.getPlanes(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			Flights.Planes _planes = (Flights.Planes) obj;
			children.addAll(_planes.getPlanes());
			return children;
		});
		explorationConsumer.put(Flights.FlightsPackage.eINSTANCE.getFlightModel(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			Flights.FlightModel _flightmodel = (Flights.FlightModel) obj;
			if(_flightmodel.getFlights() != null)
				children.add(_flightmodel.getFlights());
			if(_flightmodel.getBookings() != null)
				children.add(_flightmodel.getBookings());
			if(_flightmodel.getPersons() != null)
				children.add(_flightmodel.getPersons());
			if(_flightmodel.getRoutes() != null)
				children.add(_flightmodel.getRoutes());
			if(_flightmodel.getAirports() != null)
				children.add(_flightmodel.getAirports());
			if(_flightmodel.getPlanes() != null)
				children.add(_flightmodel.getPlanes());
			return children;
		});
		explorationConsumer.put(Flights.FlightsPackage.eINSTANCE.getAirport(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			Flights.Airport _airport = (Flights.Airport) obj;
			children.addAll(_airport.getGates());
			return children;
		});
		explorationConsumer.put(Flights.FlightsPackage.eINSTANCE.getRoutes(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			Flights.Routes _routes = (Flights.Routes) obj;
			children.addAll(_routes.getRoutes());
			return children;
		});
		explorationConsumer.put(Flights.FlightsPackage.eINSTANCE.getRoute(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			return children;
		});
		explorationConsumer.put(Flights.FlightsPackage.eINSTANCE.getAirports(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			Flights.Airports _airports = (Flights.Airports) obj;
			children.addAll(_airports.getAirports());
			return children;
		});
		explorationConsumer.put(Flights.FlightsPackage.eINSTANCE.getFlightContainer(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			Flights.FlightContainer _flightcontainer = (Flights.FlightContainer) obj;
			children.addAll(_flightcontainer.getFlights());
			return children;
		});
		explorationConsumer.put(Flights.FlightsPackage.eINSTANCE.getPerson(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			return children;
		});
		explorationConsumer.put(Flights.FlightsPackage.eINSTANCE.getPlane(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			return children;
		});
		explorationConsumer.put(Flights.FlightsPackage.eINSTANCE.getGate(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			return children;
		});
		explorationConsumer.put(Flights.FlightsPackage.eINSTANCE.getFlight(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			return children;
		});
		explorationConsumer.put(Flights.FlightsPackage.eINSTANCE.getPersons(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			Flights.Persons _persons = (Flights.Persons) obj;
			children.addAll(_persons.getPersons());
			return children;
		});
		explorationConsumer.put(Flights.FlightsPackage.eINSTANCE.getFlightObject(), obj -> {
			Collection<EObject> children = new LinkedList<>();
			return children;
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
				.match(NotificationMessage.class, this::handleNotification) //
				.match(NoMoreInput.class, this::processNotifications) //
				.build();
	}
	
	/**
	 * delegate notifications to dispatcher actor
	 * @param notification
	 */
	public void handleNotification(NotificationMessage notification) {
		long tic = System.nanoTime();
		resolveNotification(notification.notification);
		getSender().tell(true, getSelf());
		time += System.nanoTime() - tic;
	}
	
	public void processNotifications(NoMoreInput msg) {
		long tic = System.nanoTime();
		long tell_tic = System.nanoTime();
		while(!notificationCache.isEmpty()) {
			dispatchActor.tell(notificationCache.poll(), getSelf());
		}
		tell_time += System.nanoTime() - tell_tic;
		dispatchActor.tell(msg, getSelf());
		discoveredObjects = Collections.synchronizedSet(new LinkedHashSet<>());
		System.err.println("NotificationActor analysis time: " + (double) time / (double) (1000 * 1000 * 1000) + " - tell_time: " + (double) tell_time / (double) (1000 * 1000 * 1000));
	}
	
	private void resolveNotification(Notification notification) {
		switch (notification.getEventType()) {
		case Notification.ADD: {
			if(notification.getNewValue() instanceof Resource) {
				resolveAddResource(notification);
			}else {
				resolveAdd(notification);
			}
			break;
		}
		case Notification.REMOVE: {
			resolveRemove(notification);
			break;
		}
		case Notification.REMOVING_ADAPTER: {
			resolveRemoveAdapter(notification);
			break;
		}
		case Notification.RESOLVE: {
			throw new RuntimeException("Notification type RESOLVE not supported");
		}
		case Notification.SET: {
			resolveSet(notification);
			break;
		}
		case Notification.UNSET: {
			throw new RuntimeException("Notification type UNSET not supported");
		}
		case Notification.MOVE: {
			throw new RuntimeException("Notification type MOVE not supported");
		}
		case Notification.ADD_MANY: {
			resolveAddMany(notification);
			break;
		}
		case Notification.REMOVE_MANY: {
			resolveRemoveMany(notification);
			break;
		}
		default: throw new RuntimeException("Notification type id("+notification.getEventType()+") not supported");
		
		}
	}
	
	private void resolveAddResource(Notification notification) {
		Resource r = (Resource) notification.getNewValue();
		r.getContents().forEach(node -> {
			explore(node);
		});
	}
	
	private void explore(EObject rootObj) {

		if(rootObj == null) 
			return;
		
		Notification rootNotify = new ENotificationImpl(castMaybe(rootObj), Notification.ADD, null, null, rootObj);
		if(!discoveredObjects.contains(rootObj)) {
			notificationCache.add(rootNotify);
			discoveredObjects.add(rootObj);
		}
		
		Queue<EObject> frontier = new LinkedList<>();
		frontier.addAll(explorationConsumer.get(rootObj.eClass()).apply(rootObj));
		while(!frontier.isEmpty()) {
			EObject child = frontier.poll();
			Notification childNotify = new ENotificationImpl(castMaybe(child), Notification.ADD, null, null, child);
			if(!discoveredObjects.contains(child)) {
				notificationCache.add(childNotify);
				discoveredObjects.add(child);
			}
			frontier.addAll(explorationConsumer.get(child.eClass()).apply(child));
		}
		
	}
	
	private void resolveAdd(Notification notification) {
		EObject node = (EObject) notification.getNewValue();
		if(node == null) {
			return;
		}
		
		notificationCache.add(notification);
		explore(node);
		
	}
	
	private void resolveAddMany(Notification notification) {
		@SuppressWarnings("unchecked")
		List<EObject> addedNodes = (List<EObject>)notification.getNewValue();
		addedNodes.parallelStream().forEach(addedNode -> {
			Notification notify = new ENotificationImpl(castMaybe(notification.getNotifier()), Notification.ADD, (EReference)notification.getFeature(), null, addedNode);
			resolveAdd(notify);
		});
	}
	
	private void resolveRemove(Notification notification) {
		notificationCache.add(notification);
	}
	
	private void resolveRemoveMany(Notification notification) {
		@SuppressWarnings("unchecked")
		List<EObject> removedNodes = (List<EObject>)notification.getOldValue();
		for(EObject removedNode : removedNodes) {
			Notification notify = new ENotificationImpl(castMaybe(notification.getNotifier()), Notification.REMOVE, (EReference)notification.getFeature(), removedNode, null);
			resolveRemove(notify);
		}
	}
	
	private void resolveRemoveAdapter(Notification notification) {
		EContentAdapter adapter = (EContentAdapter)notification.getOldValue();
		if(adapter == null)
			return;
		notificationCache.add(notification);
	}
	
	private void resolveSet(Notification notification) {
		if(notification.getFeature() != null && notification.getFeature() instanceof EReference) {
			Notification notify = null;
			if(notification.getNewValue() == null) {
				notify = new ENotificationImpl(castMaybe(notification.getNotifier()), Notification.REMOVE, (EReference)notification.getFeature(), notification.getOldValue() , null);
				resolveRemove(notify);
			} else {
				notify = new ENotificationImpl(castMaybe(notification.getNotifier()), Notification.ADD, (EReference)notification.getFeature(), null , notification.getNewValue());
				resolveAdd(notify);
			}
			
		}else {
			notificationCache.add(notification);
		}
		
	}
	
	private static InternalEObject castMaybe(Object o) {
		if(o instanceof InternalEObject) {
			return (InternalEObject)o;
		}else {
			return null;
		}
	}

}

