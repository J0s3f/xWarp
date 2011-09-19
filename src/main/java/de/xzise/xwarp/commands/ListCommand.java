package de.xzise.xwarp.commands;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.MinecraftUtil;
import de.xzise.wrappers.permissions.Permission;
import de.xzise.xwarp.Manager;
import de.xzise.xwarp.WarpObject;
import de.xzise.xwarp.XWarp;
import de.xzise.xwarp.lister.GenericLister;
import de.xzise.xwarp.lister.ListSection;
import de.xzise.xwarp.lister.GenericLister.Column;
import de.xzise.xwarp.lister.options.EnumWhiteBlackList;
import de.xzise.xwarp.lister.options.Option;
import de.xzise.xwarp.lister.options.Options;

public abstract class ListCommand<W extends WarpObject<?>, M extends Manager<W>, C extends Enum<C>> extends DefaultSubCommand<M> {

    private final Permission<Boolean> permission;
    private final String name;
    
    protected abstract class ColumnOptions extends EnumWhiteBlackList<Column> implements Option {

        public ColumnOptions(Class<Column> enumClass) {
            super(enumClass);
            this.getWhitelist().addAll(Arrays.asList(enumClass.getEnumConstants()));
        }
    }
    
    protected final class WarpColumnOptions extends ColumnOptions {

        public WarpColumnOptions() {
            super(Column.class);
        }

        @Override
        public boolean parse(CommandSender sender, String text, boolean white) {
            if (text.equalsIgnoreCase("loc")) {
                return this.add(Column.LOCATION, white);
            } else if (text.equalsIgnoreCase("owner")) {
                return this.add(Column.OWNER, white);
            } else if (text.equalsIgnoreCase("world")) {
                return this.add(Column.WORLD, white);
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid column value: " + text);
                return true;
            }
        }
    }

    public ListCommand(M list, Server server, Permission<Boolean> permission, String name) {
        super(list, server, "list", "ls");
        this.permission = permission;
        this.name = name;
    }

    private static Integer processOptions(CommandSender sender, String value, Options<?, ?> options) {
        if (!value.isEmpty()) {
            char modifier = value.charAt(0);
            boolean white = true;
            String rawValue;
            switch (modifier) {
            case '-':
                white = false;
            case '+':
                rawValue = value.substring(1);
                break;
            default:
                rawValue = value;
            }

            String[] segments = rawValue.split(":");
            if (segments.length == 2) {
                Option option = options.get(segments[0]);
                if (option != null) {
                    if (!option.parse(sender, segments[1], white)) {
                        sender.sendMessage(ChatColor.RED + "Parameter '" + value + "' was already added.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Unknown parameter prefix: " + segments[0]);
                }
            } else {
                Integer buffer = MinecraftUtil.tryAndGetInteger(rawValue);
                if (buffer != null) {
                    return buffer;
                } else {
                    sender.sendMessage(ChatColor.RED + "Unknown parameter: " + rawValue);
                }
            }
        }
        return null;
    }

    protected abstract String[] getLegend();
    
    protected abstract Options<C, W> getOptions();
    
    protected abstract ListSection<W> createListSection(int numLines, Set<C> columns);

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (!XWarp.permissions.permission(sender, this.permission)) {
            sender.sendMessage(ChatColor.RED + "You have no permission to use this command.");
            return true;
        }

        // Special case
        if (parameters.length == 2 && parameters[1].equalsIgnoreCase("legend")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Maybe no colors here, so this command could be useless here!");
            }
            for (String line : this.getLegend()) {
                sender.sendMessage(line);
            }
        } else {
            // Parse values here
            /*
             * c:<creator> oc:<offline creator (won't be expanded)> w:<world>
             * o:<owner> oo:<offline owner (won't be expanded)> v:<visibility>
             * col:<column>
             */

            // MixedList
            Options<C, W> options = this.getOptions();
//            OwnerOptions owners = new OwnerOptions();
//            CreatorOptions creators = new CreatorOptions();
//            WorldOptions worlds = new WorldOptions();
//            VisibilityOptions visibilities = new VisibilityOptions();
//            EnumWhiteBlackList<Column> columns = new EnumWhiteBlackList<Column>(Column.class);
//
//            columns.getWhitelist().addAll(this.properties.getDefaultColumns());

            Integer page = null; // Default page = 1
            // 0 = list/ls
            for (int i = 1; i < parameters.length; i++) {
                Integer buffer = processOptions(sender, parameters[i], options);
                if (buffer != null) {
                    if (page == null) {
                        page = buffer;
                    } else {
                        sender.sendMessage(ChatColor.RED + "Found more than one page definition. Selecting first: " + buffer);
                    }
                }
            }

            if (page == null) {
                page = 1;
            }

            final List<W> warps = this.manager.getWarpObjects(sender, options);

            final int maxPages = getNumberOfPages(warps.size(), sender);
            final int numLines = MinecraftUtil.getMaximumLines(sender) - 1;

            final ListSection<W> section = this.createListSection(numLines, options.getColumns());

            if (maxPages < 1) {
                sender.sendMessage(ChatColor.RED + "There are no warps to list");
            } else if (page < 1) {
                sender.sendMessage(ChatColor.RED + "Page number can't be below 1.");
            } else if (page > maxPages) {
                sender.sendMessage(ChatColor.RED + "There are only " + maxPages + " pages of warps");
            } else {
                // Get only those warps one the page
                final int offset = (page - 1) * numLines;
                final int lines = Math.min(warps.size() - offset, numLines);
                List<W> pageWarpObjects = warps.subList(offset, offset + lines);

                section.addWarps(pageWarpObjects);

                GenericLister.listPage(page, maxPages, sender, section);
            }
        }
        return true;
    }

    private static int getNumberOfPages(int elements, CommandSender sender) {
        return (int) Math.ceil(elements / (double) (MinecraftUtil.getMaximumLines(sender) - 1));
    }

    @Override
    public String getSmallHelpText() {
        return "Shows the " + this.name + " list";
    }

    @Override
    public String getCommand() {
        return this.name + " list [filters|#page]";
    }

}
