package controls;

import java.util.*;

import com.google.gson.internal.LinkedTreeMap;

import devices.*;

public class Configuration {

    public Configuration(String configName, LinkedTreeMap<?, ?> configData, Map<String, Device> allDevices) {
        this.id = configName;
        List<Map<String, Object>> actionData = (List<Map<String, Object>>) configData.get("actions");
        actionData.stream().forEach(data->operations.add(new Operation(data, allDevices)));
        this.condition = new Condition((Map<String, Object>) configData.get("conditions"), allDevices);

    }

    private String id = "";
    public String getID() {
        return this.id;
    }

    public final Condition condition;
    public final List<Operation> operations = new ArrayList<>();

    private Boolean waiting;
    public Boolean isWaiting() {
        return this.waiting;
    }
    public void setWaiting() {
        this.waiting = true;
    }
    public void resetWaiting() {
        this.waiting = false;
    }


    
}
