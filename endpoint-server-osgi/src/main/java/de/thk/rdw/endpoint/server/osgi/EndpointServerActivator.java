package de.thk.rdw.endpoint.server.osgi;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import de.thk.rdw.endpoint.device.osgi.DeviceService;

public class EndpointServerActivator implements BundleActivator {

	private static final Logger LOGGER = Logger.getLogger(EndpointServerActivator.class.getName());

	private EndpointServer endpointServer;
	private ServiceTracker<DeviceService, DeviceService> deviceServiceTracker;

	@Override
	public void start(final BundleContext context) throws Exception {
		LOGGER.log(Level.INFO, "Starting bundle \"RDW Endpoint Server\"...");
		endpointServer = new EndpointServer();
		endpointServer.start();
		ServiceTrackerCustomizer<DeviceService, DeviceService> deviceServiceTrackerCustomizer = new ServiceTrackerCustomizer<DeviceService, DeviceService>() {

			@Override
			public DeviceService addingService(ServiceReference<DeviceService> reference) {
				LOGGER.log(Level.INFO, "Adding service \"{0}\"...", new Object[] { DeviceService.class.getName() });
				DeviceService service = context.getService(reference);
				service.toggleActuator("light");
				return service;
			}

			@Override
			public void modifiedService(ServiceReference<DeviceService> reference, DeviceService service) {
				LOGGER.log(Level.INFO, "Modifying service \"{0}\"...", new Object[] { DeviceService.class.getName() });
				// TODO Auto-generated method stub
			}

			@Override
			public void removedService(ServiceReference<DeviceService> reference, DeviceService service) {
				LOGGER.log(Level.INFO, "Removing service \"{0}\"...", new Object[] { DeviceService.class.getName() });
				context.ungetService(reference);
			}
		};
		deviceServiceTracker = new ServiceTracker<>(context, DeviceService.class, deviceServiceTrackerCustomizer);
		deviceServiceTracker.open();
		LOGGER.log(Level.INFO, "Bundle \"RDW Endpoint Server\" is started.");
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		LOGGER.log(Level.INFO, "Stopping bundle \"RDW Endpoint Server\"...");
		deviceServiceTracker.close();
		endpointServer.stop();
		LOGGER.log(Level.INFO, "Bundle \"RDW Endpoint Server\" is stopped.");
	}
}
