package de.xzise.xwarp.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

import de.xzise.xwarp.PluginProperties;
import de.xzise.xwarp.WarpManager;

public class XWPlayerListener extends PlayerListener {

    private final WarpManager manager;
    private final PluginProperties properties;

    public XWPlayerListener(WarpManager manager, PluginProperties properties) {
        this.manager = manager;
        this.properties = properties;
    }
    
    public void onPlayerMove(PlayerMoveEvent event) {
        if (this.properties.isCancelWarmUpOnMovement()) {
            if (this.manager.getWarmUp().cancelWarmUp(event.getPlayer())) {
                event.getPlayer().sendMessage(ChatColor.RED + "WarmUp was canceled due to movement!");
            }
        }
    }
}
