package controls;

import java.util.*;
import java.util.stream.Stream;

import devices.Device;

public class Command {

    public Command(Device device, String command, Map<String, Object> inputParams) {
        this.identifier = queue.size() + 1;
        this.device = device;
        this.description = command;
        this.inputParams = inputParams;
    }

    protected static List<Command> queue = new ArrayList<>();

    public final Integer identifier;
    public final Device device;
    public final String description;
    public final Map<String, ?> inputParams;

    public static void add(Command command) {
        command.setGivenTime();
        Command.queue = Stream.concat(Command.queue.stream(), Stream.of(command)).toList();
    }

    public static void remove(Command command) {
        Command.queue = Command.queue.stream().filter(cmd -> !cmd.matches(command)).toList();
    }

    public static void cleanup() {
        Command.queue = Command.queue.stream().filter(cmd -> (System.currentTimeMillis() - cmd.givenTime)<500).toList();
    }

    public boolean matches(Command command) {
            return ((this.device == command.device) && (this.description.equals(command.description)) && (this.inputParams.equals(command.inputParams)));
    }

    private Long givenTime;
    private void setGivenTime() {
        this.givenTime = System.currentTimeMillis();
    }

    public static boolean commandGiven(Command command) {
        Boolean given=false;
        for (Integer index = 0; index < Command.queue.size(); index++) {
            Command presentCommand = Command.queue.get(index);
            if (presentCommand.matches(command)) {
                given = true;
            }
        }
        Command.remove(command);
        Command.cleanup();
        return given;
    }
}
