package com.bukkit.xzise.xwarp.commands;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import me.taylorkelly.mywarp.Converter;
import me.taylorkelly.mywarp.WarpList;

public class ConvertCommand extends SubCommand {

	private boolean warning;

	public ConvertCommand(WarpList list, Server server) {
		super(list, server);
		this.warning = false;
	}

	@Override
	protected boolean internalExecute(Player player, String[] parameters) {
		if (parameters.length == 0) {
			if (!warning) {
				player.sendMessage(ChatColor.RED + "Warning: " + ChatColor.WHITE + "Only use a copy of warps.txt.");
				player.sendMessage("This will delete the warps.txt it uses");
				player.sendMessage("Use " + ChatColor.RED + "'/warp convert'" + ChatColor.WHITE
						+ " again to confirm.");
				warning = true;
			} else {
				Converter.convert(player, this.server, this.list);
				warning = false;
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int getPossibility(String[] parameters) {
		if (parameters[0].equalsIgnoreCase("convert") && parameters.length == 1) {
			return 1;
		} else {
			return -1;
		}
	}

}
