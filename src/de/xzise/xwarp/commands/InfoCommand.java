package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.Warp;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.EditorPermissions;
import de.xzise.xwarp.Permissions;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.lister.GenericLister;

public class InfoCommand extends WarpCommand {

	public InfoCommand(WarpManager list, Server server) {
		super(list, server, "", "info");
	}

	@Override
	protected boolean executeEdit(CommandSender sender, String warpName, String creator, String[] parameters) {
		Warp warp = this.list.getWarp(warpName, creator, MinecraftUtil.getPlayerName(sender));
		sender.sendMessage("Warp info: " + ChatColor.GREEN + warp.name);
		
		// Group?
		String group = MyWarp.permissions.getGroup(warp.getLocation().getWorld().getName(), warp.creator);
		String groupText = "";
		if (group != null) {
			groupText = ChatColor.WHITE + " (Group: " + ChatColor.GREEN + group + ChatColor.WHITE + ")";
		}
		
		sender.sendMessage("Creator: " + ChatColor.GREEN + warp.creator + groupText);
		String visibility = "";
		switch (warp.visibility) {
		case GLOBAL :
			visibility = "Global";
			break;
		case PUBLIC :
			visibility = "Public";
			break;
		case PRIVATE :
			visibility = "Private";
			break;
		}		
		if (sender instanceof Player) {
			visibility = GenericLister.getColor(warp, (Player) sender) + visibility;
		}
		sender.sendMessage("Visibility: " + visibility);
		//TODO: Add invitees?
//		List<String> permissions = warp.permissions;
//		String invitees = "";
//		if (permissions.size() == 0) {
//			invitees = "None";
//		} else {
//			Iterator<String> i = permissions.iterator();
//			while (i.hasNext()) {
//				String name = i.next();
//				invitees = invitees + ChatColor.GREEN + name;
//				if (i.hasNext()) {
//					invitees += ChatColor.WHITE + ", ";
//				}
//			}
//		}
//		sender.sendMessage("Invitees: " + invitees);

		String[] editors = warp.getEditors();
		String editorsLine = "";
		String inviteesLine = "";
		if (editors.length == 0) {
			editorsLine = "None";
		} else {
			for (int i = 0; i < editors.length; i++) {
				String editor = editors[i];
				EditorPermissions editorPermissions = warp.getEditorPermissions(editor);
				String permissionsString = editorPermissions.getPermissionString();
				if (!permissionsString.isEmpty()) {
					editor += " " + permissionsString;
					if (editorPermissions.get(Permissions.WARP)) {
						if (!inviteesLine.isEmpty()) {
							inviteesLine += ", ";
						}
						inviteesLine += editor;
					}
				}
				if (i < editors.length - 1) {
					editorsLine += ChatColor.WHITE + ", ";
				}
			}
		}
		sender.sendMessage("Invitees: " + (inviteesLine.isEmpty() ? "None" : inviteesLine));
		sender.sendMessage("Editors: " + editorsLine);
		
		Location location = warp.getLocation();
		sender.sendMessage("Location: World = " + ChatColor.GREEN + location.getWorld().getName() + ChatColor.WHITE + ", x = " + ChatColor.GREEN + location.getBlockX() + ChatColor.WHITE + ", y = " + ChatColor.GREEN + location.getBlockY() + ChatColor.WHITE + ", z = " + ChatColor.GREEN + location.getBlockZ());
		
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
