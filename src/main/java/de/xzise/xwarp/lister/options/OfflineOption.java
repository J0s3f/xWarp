package de.xzise.xwarp.lister.options;

import org.bukkit.command.CommandSender;

import de.xzise.MinecraftUtil;

public class OfflineOption implements Option {

    private final Option option;

    public OfflineOption(Option option) {
        this.option = option;
    }

    @Override
    public boolean parse(CommandSender sender, String text, boolean white) {
        return this.option.parse(sender, MinecraftUtil.expandName(text), white);
    }
}
