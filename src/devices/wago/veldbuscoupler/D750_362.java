package devices.wago.veldbuscoupler;

import de.re.easymodbus.exceptions.ModbusException;
import de.re.easymodbus.modbusclient.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import devices.Device;
import devices.ioAlgemeen.drukknop.*;
import controls.*;

public class D750_362 extends ModbusClient implements Device, IOParent {

    public D750_362(String deviceName, Map<String, ?> initArgs) {
        super((String) initArgs.get("host"), ((Double) initArgs.get("port")).intValue());
        this.id = deviceName;
        this.nrInputModules = ((Double) initArgs.get("nrInputModules")).intValue();
    }

    private String id;
    private Map<Integer, IOState> ioStates = new HashMap<>();
    private boolean running;
    private final int nrInputModules;
    private List<WagoIODevice> children = new ArrayList<>();

    public String getID() {
        return this.id;
    }

    public void addChild(Device device) {
        WagoIODevice child = (WagoIODevice) device;
        children.add(child);
    }

    private void updateIOStates() throws IOException, ModbusException {
        if (!super.isConnected()) {
            super.Connect();
        }
        boolean[] readResult = super.ReadDiscreteInputs(0, this.nrInputModules * 16);
        List<Boolean> ioList = new ArrayList<>();
        for (boolean i : readResult) {
            ioList.add(i);
        }
        for (int i = 0; i < ioList.size(); i++) {
            ioStates.computeIfAbsent(i,
                    index -> new IOState(index, ioList.get(index)));
            IOState ioState = ioStates.get(i);
            Boolean latestValue = ioList.get(i);
            if (!latestValue.equals(ioState.value)) {
                ioState.markAsChanged(ioList.get(i));
            } else if (System.currentTimeMillis() - ioState.changeTime >= 500 && !ioState.done) {
                ioState.markAsValid();
            }
        }

    }

    private void mapIOStatesToDeviceCommands() {
        List<IOState> validStates = this.ioStates.values().stream().filter(state -> state.valid).toList();
        List<Integer> stateIndices = validStates.stream().map(state -> state.index).toList();
        List<WagoIODevice> devices = children.stream().filter(child -> child.hasAddressInRange(stateIndices)).toList();
        Stream<Command> commands = devices.stream().map(device -> getCommand(device, validStates));
        commands.forEach(Command::add);
        validStates.stream().forEach(IOState::markAsDone);
    }

    private Command getCommand(WagoIODevice device, List<IOState> validStates) {
        Map<Integer, String> countCmdMap = new HashMap<>();

        List<IOState> btnStates = validStates.stream().filter(state -> device.containsAddress(state.index)).toList();
        Integer btnCount = btnStates.stream().map(state -> state.pressCount).toList().get(0);
        List<Integer> btnAddresses = btnStates.stream().map(state -> state.index - device.getIoAddress()).toList();
        Map<String, Object> inputParams = device.getBtnParams(btnAddresses);

        countCmdMap.put(1, "press_long");
        countCmdMap.put(2, "press_once");
        countCmdMap.put(3, "press_long_twice");
        countCmdMap.put(4, "press_twice");

        return new Command(device, countCmdMap.get(btnCount), inputParams);

    }

    @Override
    public void run() {
        this.running = true;
        while (this.running) {
            try {
                updateIOStates();
                mapIOStatesToDeviceCommands();
            } catch (IOException e) {
                e.printStackTrace();

            } catch (ModbusException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopDevice() throws IOException {
        this.running = false;
        super.Disconnect();
    }
}

class IOState {

    public IOState(int index, boolean value) {
        this.index = index;
        this.value = value;
        this.valid = false;
        this.done = true;
        this.busy = false;
        changeTime = System.currentTimeMillis();
        this.changeCount = 0;
        this.pressCount = 0;
    }

    public final int index;
    public Boolean value;
    public Boolean valid;
    public Boolean done;
    public Boolean busy;
    public long changeTime;
    public Integer changeCount;
    public Integer pressCount;

    public void markAsChanged(boolean value) {
        this.value = value;
        this.valid = false;
        this.changeCount += 1;
        this.done = false;
        changeTime = System.currentTimeMillis();
    }

    public void markAsValid() {
        if (!this.busy) {
            this.valid = true;
            this.pressCount = this.changeCount;
        } else if (this.changeCount % 2 == 0) {
            this.busy = false;
            this.markAsDone();
        }
    }

    public void markAsDone() {
        if (this.changeCount % 2 == 0) {
            this.valid = false;
            this.changeCount = 0;
            this.pressCount = 0;
            this.done = true;
            this.busy = false;
        } else {
            this.busy = true;
        }
    }

    public Boolean isPartOfDevices(List<WagoIODevice> devices) {
        return devices.stream().anyMatch(device -> device.containsAddress(index));
    }
}