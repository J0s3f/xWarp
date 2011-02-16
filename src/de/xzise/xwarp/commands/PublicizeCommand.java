package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.WarpList;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

public class PublicizeCommand extends WarpCommand {

	public PublicizeCommand(WarpList list, Server server) {
		super(list, server, false, "public");
	}

	@Override
	protected boolean executeEdit(CommandSender sender, String warpName, String creator, String parameter) {
		this.list.publicize(warpName, creator, sender);
		return true;
	}

}
