package FlightGTCEP.api;

import Flights.FlightsPackage;
import org.emoflon.ibex.common.operational.IContextPatternInterpreter;
import org.emoflon.ibex.gt.api.GraphTransformationApp;

/**
 * An application using the FlightGTCEPAPI.
 */
public class FlightGTCEPApp extends GraphTransformationApp<FlightGTCEPAPI> {

	/**
	 * Creates the application with the given engine.
	 * 
	 * @param engine
	 *            the pattern matching engine
	 */
	public FlightGTCEPApp(final IContextPatternInterpreter engine) {
		super(engine);
	}

	/**
	 * Creates the application with the given engine.
	 * 
	 * @param engine
	 *            the pattern matching engine
	 * @param workspacePath
	 *            the workspace path
	 */
	public FlightGTCEPApp(final IContextPatternInterpreter engine, final String workspacePath) {
		super(engine, workspacePath);
	}

	@Override
	public void registerMetaModels() {
		registerMetaModel(FlightsPackage.eINSTANCE);
	}

	@Override
	public FlightGTCEPAPI initAPI() {
		if (defaultResource.isPresent()) {
			return new FlightGTCEPAPI(engine, resourceSet, defaultResource.get(), workspacePath);
		}
		return new FlightGTCEPAPI(engine, resourceSet, workspacePath);
	}
}
