package openmods.whodunit.data;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import openmods.whodunit.Log;
import openmods.whodunit.Setup;
import openmods.whodunit.data.graph.GraphVisitor;
import openmods.whodunit.data.graph.GraphvizVisitor;
import openmods.whodunit.data.graph.JsonVisitor;
import openmods.whodunit.utils.CommandSyntaxException;

import com.google.common.collect.*;

public class CommandDump implements ICommand {

    private static final String COMMAND_NAME = "call_tracking";

    private static final String COMMAND_DUMP = "dump";

    private static final String COMMAND_RESET = "reset";

    private interface GraphVisitorFactory {
        public GraphVisitor create(File file);

        public String getExtension();
    }

    private final Map<String, GraphVisitorFactory> outputs = Maps.newHashMap();

    {
        outputs.put("graphviz", new GraphVisitorFactory() {
            @Override
            public String getExtension() {
                return "gv";
            }

            @Override
            public GraphVisitor create(File file) {
                return new GraphvizVisitor(file);
            }
        });

        outputs.put("compact-json", new GraphVisitorFactory() {
            @Override
            public String getExtension() {
                return "json";
            }

            @Override
            public GraphVisitor create(File file) {
                return new JsonVisitor(file, false);
            }
        });

        outputs.put("pretty-json", new GraphVisitorFactory() {
            @Override
            public String getExtension() {
                return "json";
            }

            @Override
            public GraphVisitor create(File file) {
                return new JsonVisitor(file, true);
            }
        });
    }

    private final List<String> subCommands = ImmutableList.of(COMMAND_RESET, COMMAND_DUMP);

    private final LocationManager locations;

    public CommandDump(LocationManager locations) {
        this.locations = locations;
    }

    @Override
    public int compareTo(Object o) {
        return COMMAND_NAME.compareTo(((ICommand)o).getCommandName());
    }

    @Override
    public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return COMMAND_NAME + " dump <location> <format> OR\n" +
                COMMAND_NAME + " reset";
    }

    @Override
    @SuppressWarnings("rawtypes")
    public List getCommandAliases() {
        return null;
    }

    private static final DateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

    private static File createFile(File dumpFolder, String location, String extension) {
        String date = FORMATTER.format(new Date());

        File result;
        int counter = 0;

        do {
            String filename = String.format("%s-%s-%d.%s", location, date, counter++, extension);
            result = new File(dumpFolder, filename);
        } while (result.exists());

        return result;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] command) {
        if (command.length < 1)
            throw new CommandSyntaxException();

        String subCommand = command[0];

        if (COMMAND_RESET.equalsIgnoreCase(subCommand)) {
            CallCollector.resetData();
            return;
        }

        if (command.length < 2)
            throw new CommandSyntaxException();

        String location = command[1];

        Integer locationId = locations.getIdForLocation(location);
        if (locationId == null)
            throw new CommandSyntaxException();

        if (COMMAND_DUMP.equalsIgnoreCase(subCommand)) {
            if (command.length != 3)
                throw new CommandSyntaxException();
            if (Setup.mcLocation == null)
                return;

            String format = command[2];

            GraphVisitorFactory formatFactory = outputs.get(format);
            if (formatFactory == null)
                throw new CommandSyntaxException();

            File outputFolder = Setup.getDumpDir();

            File outputFile = createFile(outputFolder, location, formatFactory.getExtension());
            GraphVisitor visitor = formatFactory.create(outputFile);

            CallCollector.visitData(visitor, ImmutableSet.of(locationId));
            Log.info("Stored data to file %s", outputFile);
        } else
            throw new CommandNotFoundException();

    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender.canCommandSenderUseCommand(4, COMMAND_NAME);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public List addTabCompletionOptions(ICommandSender sender, String[] command) {
        if (command.length == 0)
            return null;

        if (command.length == 1)
            return filterPrefixes(command[0], subCommands);
        if (command.length == 2)
            return filterPrefixes(command[1], locations.listLocationNames());

        String subCommand = command[0];
        if (COMMAND_DUMP.equals(subCommand))
            return filterPrefixes(command[2], outputs.keySet());

        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] command, int index) {
        return false;
    }

    public static List<String> filterPrefixes(String prefix, Collection<String> proposals) {
        prefix = prefix.toLowerCase();

        List<String> result = Lists.newArrayList();
        for (String s : proposals)
            if (s.startsWith(prefix))
                result.add(s);

        return result;
    }

}
