package devices;

import java.util.*;
import java.util.logging.*;

import javax.naming.NameNotFoundException;

import controls.*;

public interface Device extends Runnable {

    public List<String> locations = Collections.emptyList();

    public String getID();

    default void run() {}

    default Action getActionByName(String actionID) throws NameNotFoundException {
        throw new NameNotFoundException(actionID);
    }

    default void setDeviceState(DeviceState deviceState) {
        Logger logger = Logger.getGlobal();
        logger.log(Level.WARNING, "Set device state not implemented");
    }
    default DeviceState getDeviceState() {
        return DeviceState.UNKNOWN;
    }
}
