package devices.ioAlgemeen.drukknop;

import java.util.*;

import devices.wago.veldbuscoupler.WagoIODevice;

public class DrukknopEnkel implements WagoIODevice {

    public DrukknopEnkel(String deviceName, Map<String, Object> initArgs) {
        this.locations = (List) initArgs.get("location");
        this.id = deviceName;
        this.ioAddress = ((Double) initArgs.get("ioStartAddress")).intValue()-1;
        this.parent = (IOParent) initArgs.get("parent");
        this.parent.addChild(this);
    }

    private static final Integer NRBUTTONS = 2;

    public Integer getNrButtons() {
        return NRBUTTONS;
    }

    public String getID() {
        return this.id;
    }

    public List<String> locations = Collections.emptyList();
    private String id = "";
    public Integer ioAddress;
    public IOParent parent;

    public Integer getIoAddress() {
        return ioAddress;
    }

    public Map<String, Object> getBtnParams(List<Integer> addresses) {
        return new HashMap<>();
    }

    public Boolean containsAddress(Integer address) {
        return (address >= ioAddress) && (address <= ioAddress + getNrButtons());
    }

    public Boolean hasAddressInRange(List<Integer> range) {
        for (Integer value = ioAddress; value < ioAddress + getNrButtons(); value++) {
            if (range.contains(value)) {
                return true;
            }
        }
        return false;
    }
}
