package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.MyWarp;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.warpable.Warpable;
import de.xzise.xwarp.warpable.WarperFactory;
import de.xzise.xwarp.wrappers.permission.PermissionTypes;

/* 
 * Temporary extra command, to warp in another worlds. Maybe it work maybe not!
 */
public class WarpForceToCommand extends WarpCommand {

    public WarpForceToCommand(WarpManager list, Server server) {
        super(list, server, new String[0], "force-to", ">-t");
    }

    @Override
    protected boolean executeEdit(CommandSender sender, String warpName, String creator, String[] parameters) {
        Warpable warpable = WarperFactory.getWarpable(sender);
        if (warpable != null) {
            this.list.warpTo(warpName, creator, sender, warpable, false, true);
        }
        return true;
    }
    @Override
    protected String[] getFullHelpText() {
        return new String[] { "Warps the player to the given warp.", "This command is only ingame available." };
    }

    @Override
    protected String getSmallHelpText() {
        return "Warps the player";
    }

    @Override
    protected String getCommand() {
        return "warp force-to <name> [creator]";
    }

    @Override
    protected boolean listHelp(CommandSender sender) {
        return MyWarp.permissions.permissionOr(sender, PermissionTypes.WARP_TO_PERMISSIONS);
    }
}
