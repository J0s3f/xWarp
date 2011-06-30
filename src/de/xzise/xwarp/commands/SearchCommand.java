package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.Searcher;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.wrappers.permission.PermissionTypes;

public class SearchCommand extends DefaultSubCommand {

    public SearchCommand(WarpManager list, Server server) {
        super(list, server, "search");
    }

    @Override
    protected boolean internalExecute(CommandSender sender, String[] parameters) {
        if (parameters.length == 2 || parameters.length == 3) {
            if (!MyWarp.permissions.permission(sender, PermissionTypes.CMD_SEARCH)) {
                sender.sendMessage(ChatColor.RED + "You have no permission to search warps.");
            } else {
                Integer page = null;
                if (parameters.length == 3) {
                    page = MinecraftUtil.tryAndGetInteger(parameters[2]);
                } else {
                    page = 1;
                }
                if (page != null) {
                    Searcher searcher = new Searcher(this.list);
                    searcher.addPlayer(sender);
                    searcher.setQuery(parameters[1]);
                    searcher.search(page);
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid page number entered.");
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected String[] getFullHelpText() {
        return new String[] { "List all warps which name contains the query text." };
    }

    @Override
    protected String getSmallHelpText() {
        return "Search for " + ChatColor.GRAY + "<query>";
    }

    @Override
    protected String getCommand() {
        return "warp search <query>";
    }
}
