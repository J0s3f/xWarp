package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.Warp;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.Permissions;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.lister.GenericLister;

public class InfoCommand extends WarpCommand {

    public InfoCommand(WarpManager list, Server server) {
        super(list, server, "", "info");
    }

    @Override
    protected boolean executeEdit(CommandSender sender, String warpName, String owner, String[] parameters) {
        Warp warp = this.list.getWarp(warpName, owner, MinecraftUtil.getPlayerName(sender));
        if (warp != null) {
            sender.sendMessage("Warp info: " + ChatColor.GREEN + warp.name);
            String group = null;
            if (warp.isValid()) {
                group = MyWarp.permissions.getGroup(warp.getLocation().getWorld().getName(), warp.creator);
            } else {
                sender.sendMessage(ChatColor.RED + "The location is invalid!");
                group = MyWarp.permissions.getGroup(server.getWorlds().get(0).getName(), warp.creator);
            }

            String groupText = "";
            if (group != null) {
                groupText = ChatColor.WHITE + " (Group: " + ChatColor.GREEN + group + ChatColor.WHITE + ")";
            }

            sender.sendMessage("Owner: " + ChatColor.GREEN + warp.creator + groupText);
            String visibility = "";
            switch (warp.visibility) {
            case GLOBAL:
                visibility = "Global";
                break;
            case PUBLIC:
                visibility = "Public";
                break;
            case PRIVATE:
                visibility = "Private";
                break;
            }
            if (sender instanceof Player) {
                visibility = GenericLister.getColor(warp, (Player) sender) + visibility;
            }
            sender.sendMessage("Visibility: " + visibility);

            String[] editors = warp.getEditors();
            String editor = "";
            String invitees = "";
            if (editors.length == 0) {
                editor = "None";
            } else {
                for (int i = 0; i < editors.length; i++) {
                    String string = editors[i];
                    Permissions[] pms = warp.getEditorPermissions(string).getByValue(true);
                    if (pms.length > 0) {
                        editor += ChatColor.GREEN + string + " ";
                        char[] editorPermissions = new char[pms.length];
                        for (int j = 0; j < pms.length; j++) {
                            editorPermissions[j] = pms[j].value;
                            if (pms[j] == Permissions.WARP) {
                                if (!invitees.isEmpty()) {
                                    invitees += ", ";
                                }
                                invitees += string;
                            }
                        }
                        editor += new String(editorPermissions);
                    }
                    if (i < editors.length - 1) {
                        editor += ChatColor.WHITE + ", ";
                    }
                }
            }
            sender.sendMessage("Invitees: " + (invitees.isEmpty() ? "None" : invitees));
            sender.sendMessage("Editors: " + editor);

            if (warp.isValid()) {
                Location location = warp.getLocation();
                sender.sendMessage("Location: World = " + ChatColor.GREEN + location.getWorld().getName() + ChatColor.WHITE + ", x = " + ChatColor.GREEN + location.getBlockX() + ChatColor.WHITE + ", y = " + ChatColor.GREEN + location.getBlockY() + ChatColor.WHITE + ", z = " + ChatColor.GREEN + location.getBlockZ());
            }
        } else {
            WarpManager.sendMissingWarp(warpName, owner, sender);
        }

        return true;
    }

    @Override
    protected String[] getFullHelpText() {
        return new String[] { "Show the information about the warp." };
    }

    @Override
    protected String getSmallHelpText() {
        return "Show warp's information";
    }

}
