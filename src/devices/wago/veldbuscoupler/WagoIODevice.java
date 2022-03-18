package devices.wago.veldbuscoupler;

import java.util.*;

import devices.Device;

public interface WagoIODevice extends Device {
    public Integer getIoAddress();
    public Map<String, Object> getBtnParams(List<Integer> addresses);
    public Boolean containsAddress(Integer address);
    public Boolean hasAddressInRange(List<Integer> range);
    public Integer getNrButtons();
}
