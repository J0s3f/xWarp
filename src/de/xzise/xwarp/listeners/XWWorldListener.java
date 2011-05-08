package de.xzise.xwarp.listeners;

import me.taylorkelly.mywarp.Warp;

import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldEvent;

import de.xzise.xwarp.WarpManager;

public class XWWorldListener extends WorldListener {

    private final WarpManager manager;

    public XWWorldListener(WarpManager manager) {
        this.manager = manager;
    }

    @Override
    public void onWorldLoaded(WorldEvent event) {
        for (Warp warp : this.manager.getWarps()) {
            warp.getLocationWrapper().setWorld(event.getWorld());
        }
    }
}
