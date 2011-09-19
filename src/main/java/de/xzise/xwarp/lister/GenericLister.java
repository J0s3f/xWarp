package de.xzise.xwarp.lister;

import org.angelsl.minecraft.randomshit.fontwidth.MinecraftFontWidthCalculator;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.xzise.Callback;
import de.xzise.metainterfaces.FixedLocation;
import de.xzise.metainterfaces.LocationWrapper;
import de.xzise.xwarp.Warp;
import de.xzise.xwarp.Warp.Visibility;
import de.xzise.xwarp.warpable.WarperFactory;

public class GenericLister {

    public static final ChatColor GLOBAL_OWN = ChatColor.DARK_BLUE;
    public static final ChatColor PUBLIC_OWN = ChatColor.BLUE;
    public static final ChatColor PRIVATE_OWN = ChatColor.AQUA;

    public static final ChatColor GLOBAL_OTHER = ChatColor.DARK_GREEN;
    public static final ChatColor PUBLIC_OTHER = ChatColor.GREEN;
    public static final ChatColor PRIVATE_OTHER = ChatColor.RED;

    public static final ChatColor PRIVATE_INVITED = ChatColor.YELLOW;

    private static final Callback<Integer, String> NON_PROPORTIONAL_WIDTH = new Callback<Integer, String>() {
        @Override
        public Integer call(String text) {
            return ChatColor.stripColor(text).length();
        }
    };
    
    private static final Callback<Integer, String> INGAME_MINECRAFT_WIDTH = new Callback<Integer, String>() {
        @Override
        public Integer call(String text) {
            return MinecraftFontWidthCalculator.getStringWidth(text);
        }
    };

    //@formatter:off
    public static final String[] WPALegend = new String[] {
        ChatColor.RED + "-------------------- " + ChatColor.WHITE + "LIST LEGEND" + ChatColor.RED + " -------------------",
        GenericLister.GLOBAL_OWN + "Yours and allowed",
        GenericLister.PRIVATE_OTHER + "Not yours and not allowed",
        GenericLister.PRIVATE_INVITED + "Not yours and you are allowed"
    };

    public static final String[] WarpLegend = new String[] {
        ChatColor.RED + "-------------------- " + ChatColor.WHITE + "LIST LEGEND" + ChatColor.RED + " -------------------",
        GenericLister.GLOBAL_OWN + "Yours and it is global",
        GenericLister.PUBLIC_OWN + "Yours and it is public.",
        GenericLister.PRIVATE_OWN + "Yours and it is private.",
        GenericLister.GLOBAL_OTHER + "Not yours and it is global",
        GenericLister.PUBLIC_OTHER + "Not yours and it is public",
        GenericLister.PRIVATE_OTHER + "Not yours, private and not invited",
        GenericLister.PRIVATE_INVITED + "Not yours, private and you are invited"
    };
    //@formatter:on

    private GenericLister() { }

    public enum Column {
        OWNER,
        WORLD,
        LOCATION;
    }

    public static void listPage(int page, int maxPages, CommandSender sender, ListSection<?>... sections) {

        int charsPerLine = 40;
        Callback<Integer, String> widther = NON_PROPORTIONAL_WIDTH;

        // Get the correct width calculator!
        if (sender instanceof ConsoleCommandSender) {
            charsPerLine = 80;
            widther = NON_PROPORTIONAL_WIDTH;
        } else if (sender instanceof Player) {
            charsPerLine = 40;
            widther = INGAME_MINECRAFT_WIDTH;
        }

        // Generate header with the same length every time
        String intro = GenericLister.charList(charsPerLine / 2 - GenericLister.getWidth(page, 10), '-') + " " + ChatColor.GREEN + "Page " + page + "/" + maxPages + ChatColor.WHITE + " " + GenericLister.charList(charsPerLine / 2 - GenericLister.getWidth(maxPages, 10), '-');

        sender.sendMessage(ChatColor.WHITE + intro);

        final int width = widther.call(intro);

        for (ListSection<?> listSection : sections) {
            if (listSection.title != null && !listSection.title.isEmpty()) {
                sender.sendMessage(listSection.title);
            }
            listSection.print(sender, widther, width);
        }
    }

    /**
     * Lob shit off that string till it fits.
     */
    public static String substring(String name, int left, Callback<Integer, String> widthCalculator) {
        while (widthCalculator.call(name) > left && name.length() > 3) {
            name = name.substring(0, name.length() - 1);
        }
        return name;
    }

    public static String whitespace(int length, int spaceWidth) {
        return charList(length / spaceWidth, ' ');
    }

    public static String charList(int count, char c) {
        StringBuilder ret = new StringBuilder();

        while (count-- > 0) {
            ret.append(c);
        }

        return ret.toString();
    }

    private static int getWidth(int number, int base) {
        int width = 1;
        while (number >= base) {
            number /= base;
            width++;
        }
        return width;
    }

    public static ChatColor getColor(Warp warp, Player player) {
        return getColor(player != null && warp.isOwn(player.getName()), player != null && warp.playerCanWarp(WarperFactory.getWarpable(player)), warp.getVisibility(), player);
    }

    public static ChatColor getColor(boolean isOwn, boolean invited, Visibility visibility, Player player) {
        if (visibility == null) {
            visibility = Visibility.PUBLIC;
        }
        if (isOwn) {
            switch (visibility) {
            case PRIVATE:
                return GenericLister.PRIVATE_OWN;
            case PUBLIC:
                return GenericLister.PUBLIC_OWN;
            case GLOBAL:
                return GenericLister.GLOBAL_OWN;
            }
        } else {
            switch (visibility) {
            case PRIVATE:
                if (invited) {
                    return GenericLister.PRIVATE_INVITED;
                } else {
                    return GenericLister.PRIVATE_OTHER;
                }
            case PUBLIC:
                return GenericLister.PUBLIC_OTHER;
            case GLOBAL:
                return GenericLister.GLOBAL_OTHER;
            }
        }
        return GenericLister.PRIVATE_OTHER;
    }

    public static String getLocationString(LocationWrapper wrapper, boolean world, boolean coordinates) {
        if (world || coordinates) {
            FixedLocation location = wrapper.getLocation();
            StringBuilder result = new StringBuilder("@(");
            if (world) {
                result.append(wrapper.getWorld());
                if (!wrapper.isValid()) {
                    result.append(" " + ChatColor.RED + "(invalid)" + ChatColor.WHITE);
                }
                if (coordinates) {
                    result.append(", ");
                }
            }
            if (coordinates) {
                result.append(location.getBlockX()).append(", ");
                result.append(location.getBlockY()).append(", ");
                result.append(location.getBlockZ());
            }
            return result.append(")").toString();
        } else {
            return "";
        }
    }

}