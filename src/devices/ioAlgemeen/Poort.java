package devices.ioAlgemeen;

import java.util.*;

import javax.naming.NameNotFoundException;

import controls.Action;
import devices.*;
import devices.ioAlgemeen.drukknop.IOParent;

public class Poort implements Device {

    public Poort(String deviceName, Map<String, ?> initArgs) {
        this.id = deviceName;
        this.ioAddress = ((Double) initArgs.get("ioAddress")).intValue()+511;
        this.parent = (IOParent) initArgs.get("parent");
    }

    private String id;
    public String getID() {
        return this.id;
    }

    private Integer ioAddress;
    private IOParent parent;

    @Override
    public DeviceState getDeviceState() {
        return DeviceState.UNKNOWN;
    }
    
    static final Action trigger = new Action() {
        public void run() {
            Poort poort = (Poort) this.device;
            try {
                poort.parent.WriteSingleCoil(poort.ioAddress, true);
                Thread.sleep(100);
                poort.parent.WriteSingleCoil(poort.ioAddress, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public Runnable init(Device device, Map<String, Object> outputParams) {
            this.device = device;
            return this;
        }

        private Device device;
    };

    @Override
    public Action getActionByName(String name) throws NameNotFoundException {
        if (name.equals("trigger")) {
            return Poort.trigger;
        } else {
            throw new NameNotFoundException(name);
        }
    }



}
