package devices;

public class DeviceState {

    public DeviceState(String value) {
        this.value = value;
    }

    public String value;

    public Boolean equalsState(DeviceState deviceState) {
        return this.value.equals(deviceState.value);
    }

    public static final DeviceState ON = new DeviceState("On");
    public static final DeviceState OFF = new DeviceState("Off");
    public static final DeviceState UNKNOWN = new DeviceState("Unknown");
    public static final DeviceState CLOSED = new DeviceState("Closed");
    public static final DeviceState OPEN = new DeviceState("Open");

}
