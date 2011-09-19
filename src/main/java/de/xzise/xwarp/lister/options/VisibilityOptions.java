package de.xzise.xwarp.lister.options;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.Warp;
import de.xzise.xwarp.Warp.Visibility;

public class VisibilityOptions extends WarpObjectOptions<Visibility, Warp> {

    @Override
    public Visibility getValue(Warp warp) {
        return warp.getVisibility();
    }

    @Override
    public boolean parse(CommandSender sender, String text, boolean white) {
        Visibility v = Visibility.parseString(text);
        if (v != null) {
            return this.add(v, white);
        } else {
            sender.sendMessage(ChatColor.RED + "Invalid visibility value: " + text);
            return true;
        }
    }

}