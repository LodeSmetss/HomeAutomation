package devices.ioAlgemeen.drukknop;

import java.util.*;
import java.util.stream.Collectors;

public class DrukknopDubbel extends DrukknopEnkel {

    public DrukknopDubbel(String deviceName, Map<String, Object> initArgs) {
        super(deviceName, initArgs);
    }

    private static final Integer NRBUTTONS = 2;

    @Override
    public Integer getNrButtons() {
        return NRBUTTONS;
    }

    @Override
    public Map<String, Object> getBtnParams(List<Integer> addresses) {
        Map<String, Object> params = new HashMap<>();
        List<String> strAddresses = addresses.stream().map(address -> address.toString()).toList();
        params.put("buttons", strAddresses.stream().sorted().collect(Collectors.joining()).replace("0", "l").replace("1", "r"));
        return params;
    }
    
}
