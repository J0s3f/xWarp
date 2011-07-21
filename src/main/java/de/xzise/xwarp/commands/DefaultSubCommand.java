package de.xzise.xwarp.commands;

import org.bukkit.Server;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.WarpManager;

/**
 * Command like list/create etc.
 * 
 * @author Fabian Neundorf.
 */
public abstract class DefaultSubCommand extends SubCommand {

    protected final WarpManager list;
    protected final Server server;

    /**
     * Creates a subcommand.
     * 
     * @param list
     *            The list to all warps.
     * @param server
     *            The server instance.
     * @param commands
     *            The commands.
     * @throws IllegalArgumentException
     *             If commands is empty.
     */
    protected DefaultSubCommand(WarpManager list, Server server, String... commands) {
        super(commands);
        this.list = list;
        this.server = server;
    }

    protected String getPlayer(String name) {
        return MinecraftUtil.expandName(name, this.server);
    }
}
