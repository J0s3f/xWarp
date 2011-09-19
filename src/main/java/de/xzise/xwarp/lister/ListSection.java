package de.xzise.xwarp.lister;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bukkit.command.CommandSender;

import de.xzise.Callback;
import de.xzise.MinecraftUtil;
import de.xzise.xwarp.WarpObject;

public abstract class ListSection<W extends WarpObject<?>> implements Iterable<W> {

    public final String title;
    private final List<W> warps;

    public ListSection(String title, int maximumLength) {
        this.title = title;
        this.warps = new ArrayList<W>(maximumLength);
    }

    public ListSection(String title) {
        this(title, MinecraftUtil.PLAYER_LINES_COUNT);
    }

    public void addWarp(W warp) {
        this.warps.add(warp);
    }

    public void addWarps(Collection<W> warp) {
        this.warps.addAll(warp);
    }

    @Override
    public Iterator<W> iterator() {
        return this.warps.iterator();
    }
    
    protected abstract void print(W warpObject, CommandSender sender, Callback<Integer, String> widther, int width);
    
    public void print(CommandSender sender, Callback<Integer, String> widther, int width) {
        for (W warpObject : this) {
            this.print(warpObject, sender, widther, width);
        }
    }

}
