package de.xzise.xwarp.lister;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableSet;

import de.xzise.Callback;
import de.xzise.xwarp.Warp;
import de.xzise.xwarp.lister.GenericLister.Column;

public class WarpListSection extends ListSection<Warp> {

    private final ImmutableSet<Column> columns;

    public WarpListSection(String title, int maximumLength, Set<Column> columns) {
        super(title, maximumLength);
        this.columns = ImmutableSet.copyOf(columns);
    }

    @Override
    public void print(Warp warp, CommandSender sender, Callback<Integer, String> widther, int width) {
        String name = warp.getName();

        String owner = warp.getOwner();
        ChatColor color;
        if (sender instanceof Player) {
            if (owner.equalsIgnoreCase(((Player) sender).getName())) {
                owner = "you";
            }
            color = GenericLister.getColor(warp, (Player) sender);
        } else {
            color = GenericLister.getColor(warp, null);
        }

        String location = GenericLister.getLocationString(warp.getLocationWrapper(), columns.contains(Column.WORLD), columns.contains(Column.LOCATION));
        final String creatorString = columns.contains(Column.OWNER) ? " by " + owner : "";

        // Find remaining length left
        int left = width - widther.call("''" + creatorString + location);

        int nameLength = widther.call(name);
        if (left > nameLength) {
            name = "'" + name + "'" + ChatColor.WHITE + creatorString + GenericLister.whitespace(left - nameLength, widther.call(" "));
        } else if (left < nameLength) {
            name = "'" + GenericLister.substring(name, left, widther) + ChatColor.WHITE + "..." + color + "'";
            nameLength = widther.call(name);
            // Cut location if needed
            location = GenericLister.substring(location, width - nameLength - widther.call(creatorString), widther);
            name += ChatColor.WHITE + creatorString;
        }

        sender.sendMessage(color + name + location);
    }

}
