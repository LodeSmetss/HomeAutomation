package controls;

import java.util.*;

import devices.*;

public interface Action extends Runnable {
    public Runnable init(Device device, Map<String, Object> outputParams);
}