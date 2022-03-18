package devices.ioAlgemeen.drukknop;

import devices.Device;

public interface IOParent {
    public void addChild(Device device);
    public void WriteSingleCoil(int startingAddress, boolean value) throws Exception;
    public boolean[] ReadCoils(int startingAddress, int quantity) throws Exception;
}
