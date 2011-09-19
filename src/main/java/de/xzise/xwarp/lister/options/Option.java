package de.xzise.xwarp.lister.options;

import org.bukkit.command.CommandSender;

public interface Option {

    boolean parse(CommandSender sender, String text, boolean white);
}
