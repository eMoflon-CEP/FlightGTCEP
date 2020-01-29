package FlightGTCEP.api;

import org.emoflon.ibex.gt.democles.runtime.DemoclesGTEngine;

/**
 * An application using the FlightGTCEPAPI with Democles.
 */
public class FlightGTCEPDemoclesApp extends FlightGTCEPApp {

	/**
	 * Creates the application with Democles.
	 */
	public FlightGTCEPDemoclesApp() {
		super(new DemoclesGTEngine());
	}

	/**
	 * Creates the application with Democles.
	 * 
	 * @param workspacePath
	 *            the workspace path
	 */
	public FlightGTCEPDemoclesApp(final String workspacePath) {
		super(new DemoclesGTEngine(), workspacePath);
	}
}
