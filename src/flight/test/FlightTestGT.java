package flight.test;

import flight.monitor.FlightMonitor;
import flight.puregt.FlightGTMonitor;

public class FlightTestGT extends FlightTest {

	@Override
	public FlightMonitor getMonitor() {
		return new FlightGTMonitor();
	}

}
