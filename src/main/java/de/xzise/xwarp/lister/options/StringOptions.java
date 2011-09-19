package de.xzise.xwarp.lister.options;

import org.bukkit.command.CommandSender;

import de.xzise.xwarp.WarpObject;

public abstract class StringOptions<W extends WarpObject<?>> extends WarpObjectOptions<String, W> {
    public boolean add(String string, boolean white) {
        return super.add(string.toLowerCase(), white);
    }
    
    @Override
    public boolean parse(CommandSender sender, String text, boolean white) {
        return this.add(text, white);
    }

    public abstract String getString(W warpObject);

    @Override
    public String getValue(W warpObject) {
        return this.getString(warpObject).toLowerCase();
    }
}