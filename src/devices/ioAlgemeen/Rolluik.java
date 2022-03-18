package devices.ioAlgemeen;

import java.util.*;

import javax.naming.NameNotFoundException;

import controls.Action;
import devices.*;
import devices.ioAlgemeen.drukknop.IOParent;

public class Rolluik implements Device {

    public Rolluik(String deviceName, Map<String, ?> initArgs) {
        this.id = deviceName;
        this.closeAddress = ((Double) initArgs.get("ioAddress")).intValue() + 511;
        this.openAddress = this.closeAddress + 1;
        this.closingTime = ((Double) initArgs.get("closingTime")).intValue() * 1000;
        this.parent = (IOParent) initArgs.get("parent");
        this.deviceState = DeviceState.UNKNOWN;
    }

    private String id;

    public String getID() {
        return this.id;
    }

    private Integer closeAddress;
    private Integer openAddress;
    private Integer closingTime;
    private DeviceState deviceState;
    private IOParent parent;

    @Override
    public DeviceState getDeviceState() {
        return this.deviceState;
    }

    @Override
    public void setDeviceState(DeviceState state) {
        this.deviceState = state;
    }

    static final Action openShutter = new Action() {
        public void run() {
            Rolluik rolluik = (Rolluik) this.device;
            try {
                rolluik.parent.WriteSingleCoil(rolluik.closeAddress, false);
                Thread.sleep(100);
                rolluik.parent.WriteSingleCoil(rolluik.openAddress, true);
                Thread.sleep(rolluik.closingTime);
                rolluik.parent.WriteSingleCoil(rolluik.openAddress, false);
                rolluik.deviceState = DeviceState.OPEN;
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

    static final Action closeShutter = new Action() {
        public void run() {
            Rolluik rolluik = (Rolluik) this.device;
            try {
                rolluik.parent.WriteSingleCoil(rolluik.openAddress, false);
                Thread.sleep(100);
                rolluik.parent.WriteSingleCoil(rolluik.closeAddress, true);
                Thread.sleep(rolluik.closingTime);
                rolluik.parent.WriteSingleCoil(rolluik.closeAddress, false);
                rolluik.deviceState = DeviceState.OPEN;
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

    static final Action stop = new Action() {
        public void run() {
            Rolluik rolluik = (Rolluik) this.device;
            try {
                rolluik.parent.WriteSingleCoil(rolluik.closeAddress, false);
                rolluik.parent.WriteSingleCoil(rolluik.openAddress, false);
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
        if (name.equals("open_shutter")) {
            return Rolluik.openShutter;
        } else if (name.equals("close_shutter")) {
            return Rolluik.closeShutter;
        } else if (name.equals("stop")) {
            return Rolluik.stop;
        } else {
            throw new NameNotFoundException(name);
        }
    }

}
