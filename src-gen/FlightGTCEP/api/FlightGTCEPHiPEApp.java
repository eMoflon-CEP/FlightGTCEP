package FlightGTCEP.api;

import org.emoflon.ibex.gt.hipe.runtime.HiPEGTEngine;

/**
 * An application using the FlightGTCEPAPI with HiPE.
 */
public class FlightGTCEPHiPEApp extends FlightGTCEPApp {

	/**
	 * Creates the application with HiPE.
	 */
	public FlightGTCEPHiPEApp() {
		super(new HiPEGTEngine());
	}

	/**
	 * Creates the application with HiPE.
	 * 
	 * @param workspacePath
	 *            the workspace path
	 */
	public FlightGTCEPHiPEApp(final String workspacePath) {
		super(new HiPEGTEngine(), workspacePath);
	}
}
