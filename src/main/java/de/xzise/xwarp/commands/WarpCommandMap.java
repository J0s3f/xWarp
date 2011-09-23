package de.xzise.xwarp.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Server;

import de.xzise.commands.CommonCommandMap;
import de.xzise.commands.CommonHelpCommand;
import de.xzise.commands.SubCommand;
import de.xzise.wrappers.economy.EconomyHandler;
import de.xzise.xwarp.PluginProperties;
import de.xzise.xwarp.Warp;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.commands.warp.CooldownCommand;
import de.xzise.xwarp.commands.warp.CreateCommand;
import de.xzise.xwarp.commands.warp.GlobalizeCommand;
import de.xzise.xwarp.commands.warp.InfoCommand;
import de.xzise.xwarp.commands.warp.ListCommand;
import de.xzise.xwarp.commands.warp.ListedCommand;
import de.xzise.xwarp.commands.warp.MessageCommand;
import de.xzise.xwarp.commands.warp.PriceCommand;
import de.xzise.xwarp.commands.warp.PrivatizeCommand;
import de.xzise.xwarp.commands.warp.PublicizeCommand;
import de.xzise.xwarp.commands.warp.SafetyCheckCommand;
import de.xzise.xwarp.commands.warp.SearchCommand;
import de.xzise.xwarp.commands.warp.ShowMessageCommand;
import de.xzise.xwarp.commands.warp.UpdateCommand;
import de.xzise.xwarp.commands.warp.WarmUpCommand;
import de.xzise.xwarp.commands.warp.WarpForceToCommand;
import de.xzise.xwarp.commands.warp.WarpToCommand;
import de.xzise.xwarp.dataconnections.DataConnection;

public class WarpCommandMap extends CommonCommandMap {

    public static final String LABEL = "warp";

    public WarpCommandMap(WarpManager manager, EconomyHandler economyWrapper, Server server, DataConnection data, File pluginPath, PluginProperties properties) {
        super();

        CommonHelpCommand helper = new CommonHelpCommand("xWarp");
        WarpToCommand warper = new WarpToCommand(manager, server);

        Collection<SubCommand> subCommands = new ArrayList<SubCommand>();

        subCommands.add(warper);
        subCommands.add(CreateCommand.newCreatePrivate(manager, server));
        subCommands.add(CreateCommand.newCreatePublic(manager, server));
        subCommands.add(CreateCommand.newCreateGlobal(manager, server));
        subCommands.add(new DeleteCommand<Warp, WarpManager>(manager, server, LABEL));
        subCommands.add(new UpdateCommand(manager, server));
        subCommands.add(new RenameCommand<Warp, WarpManager>(manager, server, LABEL));
        subCommands.add(helper);
        subCommands.add(new UninviteCommand<Warp, WarpManager>(manager, server, LABEL));
        subCommands.add(new InviteCommand<Warp, WarpManager>(manager, server, LABEL));
        subCommands.add(new GiveCommand<Warp, WarpManager>(manager, server, LABEL));
        subCommands.add(new MessageCommand(manager, server));
        subCommands.add(new ShowMessageCommand(manager, server, properties));
        subCommands.add(new AddEditorCommand<Warp, WarpManager>(manager, server, LABEL));
        subCommands.add(new RemoveEditorCommand<Warp, WarpManager>(manager, server, LABEL));
        subCommands.add(new PrivatizeCommand(manager, server));
        subCommands.add(new PublicizeCommand(manager, server));
        subCommands.add(new GlobalizeCommand(manager, server));
        subCommands.add(new WarmUpCommand(manager, server));
        subCommands.add(new CooldownCommand(manager, server));
        subCommands.add(new PriceCommand(manager, server));
        subCommands.add(new SearchCommand(manager, server));
        subCommands.add(new ListCommand(manager, server, properties));
        subCommands.add(new ChangeWorldCommand(manager, server, LABEL));
        subCommands.add(new ChangeCreatorCommand<Warp, WarpManager>(manager, server, LABEL));
        subCommands.add(new InfoCommand(manager, server, economyWrapper));
        subCommands.add(new SafetyCheckCommand(manager, server));
        subCommands.add(new ListedCommand(manager, server));
        subCommands.add(new WarpForceToCommand(manager, server));

        this.populate(subCommands);

        this.setDefault(warper);
        this.setHelper(helper);
    }
}