package de.thk.rdw.endpoint.pi4j.osgi.resources;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

import de.thk.rdw.endpoint.pi4j.osgi.resources.gpio.LedResource;
import de.thk.rdw.endpoint.pi4j.osgi.resources.gpio.MercurySwitchResource;
import de.thk.rdw.endpoint.pi4j.osgi.resources.gpio.PirSensorResource;
import de.thk.rdw.endpoint.pi4j.osgi.resources.gpio.TactileSwitchResource;

public class DeviceResourceFactory {

	private static final Logger LOGGER = Logger.getLogger(DeviceResourceFactory.class.getName());

	private DeviceResourceFactory() {
	}

	// ####################
	// SENSORS
	// ####################

	public static SensorResource createMercurySwitch(String name, DeviceResourceListener listener,
			GpioController gpioController, String pinString) {

		SensorResource result = null;
		Pin pin = parsePin(pinString);
		try {
			validateGpioParameters(name, gpioController, pin);
			result = new MercurySwitchResource(name, listener, gpioController, pin);
		} catch (IllegalArgumentException e) {
			LOGGER.log(Level.WARNING, "Could not create GPIO resource [name={0},pin={1}]. {2}",
					new Object[] { name, pinString, e.getMessage() });
		}
		return result;
	}

	public static SensorResource createTactileSwitch(String name, DeviceResourceListener listener,
			GpioController gpioController, String pinString) {

		SensorResource result = null;
		Pin pin = parsePin(pinString);
		try {
			validateGpioParameters(name, gpioController, pin);
			result = new TactileSwitchResource(name, listener, gpioController, pin);
		} catch (IllegalArgumentException e) {
			LOGGER.log(Level.WARNING, "Could not create GPIO resource [name={0},pin={1}]. {2}",
					new Object[] { name, pinString, e.getMessage() });
		}
		return result;
	}

	public static SensorResource createPirSensor(String name, DeviceResourceListener listener,
			GpioController gpioController, String pinString) {

		SensorResource result = null;
		Pin pin = parsePin(pinString);
		try {
			validateGpioParameters(name, gpioController, pin);
			result = new PirSensorResource(name, listener, gpioController, pin);
		} catch (IllegalArgumentException e) {
			LOGGER.log(Level.WARNING, "Could not create GPIO resource [name={0},pin={1}]. {2}",
					new Object[] { name, pinString, e.getMessage() });
		}
		return result;
	}

	// ####################
	// ACTUATORS
	// ####################

	public static ActuatorResource createLed(String name, DeviceResourceListener listener,
			GpioController gpioController, String pinString) {

		ActuatorResource result = null;
		Pin pin = parsePin(pinString);
		try {
			validateGpioParameters(name, gpioController, pin);
			result = new LedResource(name, listener, gpioController, pin);
		} catch (IllegalArgumentException e) {
			LOGGER.log(Level.WARNING, "Could not create GPIO resource [name={0},pin={1}]. {2}",
					new Object[] { name, pinString, e.getMessage() });
		}
		return result;
	}

	private static void validateGpioParameters(String name, GpioController gpioController, Pin pin) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Sensor name is null or empty.");
		}
		if (gpioController == null) {
			throw new IllegalArgumentException("GPIO controller is null.");
		}
		if (pin == null) {
			throw new IllegalArgumentException("Pin is invalid.");
		}
	}

	private static Pin parsePin(String pin) {
		Pin result = null;
		try {
			int pinNr = Integer.parseInt(pin);
			result = RaspiPin.getPinByAddress(pinNr);
		} catch (NumberFormatException e) {
			// Do nothing.
		}
		return result;
	}
}