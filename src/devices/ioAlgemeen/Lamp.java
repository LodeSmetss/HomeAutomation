package devices.ioAlgemeen;

import java.util.*;

import javax.naming.NameNotFoundException;

import controls.*;
import devices.*;
import devices.ioAlgemeen.drukknop.IOParent;

public class Lamp implements Device {

    public Lamp(String deviceName, Map<String, ?> initArgs) {
        this.id = deviceName;
        this.ioAddress = ((Double) initArgs.get("ioAddress")).intValue()+511;
        this.parent = (IOParent) initArgs.get("parent");
    }

    private Integer ioAddress;

    private String id;
    public String getID() {
        return this.id;
    }

    private IOParent parent;

    @Override
    public DeviceState getDeviceState() {

        try {
            boolean[] readResult = parent.ReadCoils(this.ioAddress, 1);
            for (boolean i : readResult) {
                if (i) {
                    return DeviceState.ON;
                } else {
                    return DeviceState.OFF;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DeviceState.OFF;
    }
    public void updateDeviceState() {
        this.getDeviceState();
    }
    @Override
    public void setDeviceState(DeviceState deviceState) {
        Runnable runnable;
        if (deviceState.equals(DeviceState.ON)) {
            runnable = switch_off.init(this, null);
        } else {
            runnable = switch_on.init(this, null);
        }
        Thread t = new Thread(runnable);
        t.start();
    }
    
    static final Action switch_on = new Action() {
        public void run() {
            Lamp lamp = (Lamp) this.device;
            try {
                lamp.parent.WriteSingleCoil(lamp.ioAddress, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            lamp.updateDeviceState();
        }

        public Runnable init(Device device, Map<String, Object> outputParams) {
            this.device = device;
            return this;
        }

        private Device device;
    };

    static final  Action switch_off = new Action() {
        public void run() {
            Lamp lamp = (Lamp) this.device;
            try {
                lamp.parent.WriteSingleCoil(lamp.ioAddress, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            lamp.updateDeviceState();
        }

        public Runnable init(Device device, Map<String, Object> outputParams) {
            this.device = device;
            return this;
        }

        private Device device;

    };
    
    static final Action switch_lamp = new Action() {
        public void run() {
            Lamp lamp = (Lamp) this.device;
            Boolean writeValue = !lamp.getDeviceState().equalsState(DeviceState.ON);
            try {
                lamp.parent.WriteSingleCoil(lamp.ioAddress, writeValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
            lamp.updateDeviceState();
        }

        public Runnable init(Device device, Map<String, Object> outputParams) {
            this.device = device;
            return this;
        }

        Device device;
    };

    @Override
    public Action getActionByName(String name) throws NameNotFoundException {
        if (name.equals("switch_on")) {
            return Lamp.switch_on;
        } else if (name.equals("switch_off")) {
            return Lamp.switch_off;
        } else if (name.equals("switch_lamp")) {
            return Lamp.switch_lamp;
        } else {
            throw new NameNotFoundException(name);
        }
    }

}
