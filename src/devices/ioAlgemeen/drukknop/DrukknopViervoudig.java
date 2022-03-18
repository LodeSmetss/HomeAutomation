package devices.ioAlgemeen.drukknop;

import java.util.*;
import java.util.stream.Collectors;

public class DrukknopViervoudig extends DrukknopEnkel {

    public DrukknopViervoudig(String deviceName, Map<String, Object> initArgs) {
        super(deviceName, initArgs);
    }

    private static final Integer NRBUTTONS = 4;

    @Override
    public Integer getNrButtons() {
        return NRBUTTONS;
    }

    @Override
    public Map<String, Object> getBtnParams(List<Integer> addresses) {
        Map<String, Object> params = new HashMap<>();
        List<String> strAddresses = addresses.stream().map(address -> address.toString()).toList();
        params.put("buttons", strAddresses.stream().sorted().collect(Collectors.joining())
                .replace("0", "lb")
                .replace("1", "rb")
                .replace("2", "ro")
                .replace("3", "lo"));
        return params;
    }

}
