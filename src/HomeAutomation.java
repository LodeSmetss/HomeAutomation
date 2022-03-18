import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.*;

import java.lang.reflect.*;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import devices.Device;
import devices.ioAlgemeen.*;
import devices.ioAlgemeen.drukknop.*;
import devices.wago.veldbuscoupler.D750_362;

import controls.*;

class HomeAutomation {

    private static Logger logger = Logger.getGlobal();
    protected static final HashMap<String, Device> DEVICES = constructDevices();
    protected static final HashMap<String, Configuration> CONFIGS = constructConfigs();
    private static Boolean running = true;

    public static void main(String[] args) {

        DEVICES.values().forEach((device) -> {
            Thread t = new Thread(device);
            t.start();
        });

        while (HomeAutomation.running) {

            for (Configuration config : CONFIGS.values()) {
                if (config.condition.isValid()) {
                    for (Operation operation : config.operations) {
                        operation.execute();
                    }
                }
            }

        }

    }

    private static HashMap<String, Device> constructDevices() {

        HashMap<String, Device> devices = new HashMap<>();

        HashMap<String, LinkedTreeMap<?, ?>> deviceMap = getMapFromJson("src/data/added_devices.json");

        deviceMap.forEach((deviceName, data) -> {

            try {
                if (!devices.containsKey(deviceName)) {
                    if (data.containsKey("parent")) {
                        String parentName = (String) data.get("parent");
                        Device parent = devices.get(parentName);
                        if (parent == null) {
                            Map<String, Object> parentData = (Map<String, Object>) deviceMap.get(parentName);
                            createDevice(parentName, parentData, deviceMap, devices);
                            parent = devices.get(parentName);
                        }
                    }
                    Map<String, Object> deviceData = (Map<String, Object>) data;
                    createDevice(deviceName, deviceData, deviceMap, devices);
                }
            } catch (ClassNotFoundException e) {
                logger.log(Level.WARNING, "Can not find a device class for %s".formatted(deviceName));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        return devices;

    }

    private static void createDevice(String deviceName, Map<String, Object> deviceData,
            HashMap<String, LinkedTreeMap<?, ?>> globalData, Map<String, Device> devices) throws Exception {
        String deviceDir = (String) deviceData.get("deviceDir");
        String classname = "devices" + deviceDir.replace("\\", ".");
        Class<?> deviceClass = Class.forName(classname);
        Constructor<?> constructor = deviceClass.getDeclaredConstructor(String.class, Map.class);
        Map<String, Object> deviceArgs = deviceData;
        if (deviceArgs.containsKey("parent")) {
            String parentName = (String) deviceArgs.get("parent");
            Device parent = devices.get(parentName);
            if (parent == null) {
                Map<String, Object> parentData = (Map<String, Object>) globalData.get(parentName);
                createDevice(parentName, parentData, globalData, devices);
            }
            deviceArgs.put("parent", parent);
        }
        Device deviceInst = (Device) constructor.newInstance(deviceName, deviceArgs);
        devices.put(deviceName, deviceInst);
    }

    private static HashMap<String, Configuration> constructConfigs() {

        HashMap<String, Configuration> configs = new HashMap<>();

        HashMap<String, LinkedTreeMap<?, ?>> configMap = getMapFromJson("src/data/configuration.json");

        configMap.forEach((configName, configData) -> configs.put(configName,
                new Configuration(configName, configData, DEVICES)));

        return configs;

    }

    private static HashMap<String, LinkedTreeMap<?, ?>> getMapFromJson(String filepath) {

        HashMap<String, LinkedTreeMap<?, ?>> map = new HashMap<>();

        try {
            // create Gson instance
            Gson gson = new Gson();

            // create a reader
            Reader reader = Files.newBufferedReader(Paths.get(filepath));

            // convert JSON file to map
            map = gson.fromJson(reader, HashMap.class);

            // close reader
            reader.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return map;

    }

}
