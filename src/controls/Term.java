package controls;

import java.util.Map;

import devices.*;

public class Term {

    public Term(Map<String, Object> data, Map<String, Device> allDevices) {
        this.device = allDevices.get(data.get("device"));
        this.inputParams = (Map<String, Object>) data.get("inputParams");
        this.command = new Command(device, (String) data.get("command"), inputParams);
        this.deviceState = new DeviceState((String) data.get("deviceState"));
    }
    private Device device;
    private Command command;
    private Map<String, Object> inputParams;
    private DeviceState deviceState;

    public Boolean isValid() {
        if (this.device == null) {
            return false;
        }
        return Command.commandGiven(this.command) || this.device.getDeviceState().equalsState(this.deviceState);
    }

}
