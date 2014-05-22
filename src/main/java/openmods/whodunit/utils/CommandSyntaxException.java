package openmods.whodunit.utils;

import net.minecraft.command.CommandException;

public class CommandSyntaxException extends CommandException {
    private static final long serialVersionUID = 1950508886285899691L;

    public CommandSyntaxException() {
        super("commands.generic.syntax");
    }
}
