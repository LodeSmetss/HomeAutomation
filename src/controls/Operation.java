package controls;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NameNotFoundException;

import devices.*;

public class Operation {
    
    public Operation(Map<String, Object> data, Map<String, Device> allDevices) {
        this.device = allDevices.get(data.get("device"));
        try {
            this.action = this.device.getActionByName((String) data.get("action"));
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            Operation.logger.log(Level.WARNING, "Device Not Found: %s".formatted(data.get("device")));
        }
        this.outputParams = (Map<String, Object>) data.get("outputParams");
        this.deviceState = new DeviceState((String) data.get("deviceState"));
    }

    private Action action;
    private Device device;
    private Map<String, Object> outputParams;
    private DeviceState deviceState;
    private static final Logger logger = Logger.getGlobal();

    public void execute() {
        if (this.action!=null) {
            new Thread(this.action.init(this.device, this.outputParams)).start();
        }
        if (this.deviceState.value != null) {
            this.device.setDeviceState(this.deviceState);
        }
    }



}
