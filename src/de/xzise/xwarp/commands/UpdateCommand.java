package de.xzise.xwarp.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.xwarp.WarpManager;

public class UpdateCommand extends WarpCommand {

	public UpdateCommand(WarpManager list, Server server) {
		super(list, server, "", "update", "*");
	}

	@Override
	protected boolean executeEdit(CommandSender sender, String warpName, String creator, String parameter) {
		if (sender instanceof Player) {
			this.list.updateLocation(warpName, creator, (Player) sender);
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected String[] getFullHelpText() {
		return new String[] { "Updates the position of the warp." };
	}

	@Override
	protected String getSmallHelpText() {
		return "Updates the warp's position.";
	}
}
