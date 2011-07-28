package de.xzise.xwarp;

import java.io.File;
import java.io.IOException;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.java.JavaPlugin;

import de.xzise.MinecraftUtil;
import de.xzise.XLogger;
import de.xzise.wrappers.permissions.PermissionsHandler;
import de.xzise.wrappers.economy.EconomyHandler;
import de.xzise.xwarp.commands.WPACommandMap;
import de.xzise.xwarp.commands.WarpCommandMap;
import de.xzise.xwarp.dataconnections.DataConnection;
import de.xzise.xwarp.listeners.XWBlockListener;
import de.xzise.xwarp.listeners.XWEntityListener;
import de.xzise.xwarp.listeners.XWPlayerListener;
import de.xzise.xwarp.listeners.XWWorldListener;

public class XWarp extends JavaPlugin {

    public static PermissionsHandler permissions;
    public static XLogger logger;

    private EconomyHandler economyWrapper;
    private PermissionsHandler permissionsWrapper = permissions;

    private DataConnection dataConnection;

    public String name;
    public String version;

    public XWarp() {
        super();
    }

    @Override
    public void onDisable() {
        this.dataConnection.free();
    }

    @Override
    public void onEnable() {
        this.name = this.getDescription().getName();
        this.version = this.getDescription().getVersion();
        logger = new XLogger(this);

        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }

        if (new File("MyWarp").exists() && new File("MyWarp", "warps.db").exists()) {
            this.updateFiles();
        } else {
            File old = new File("homes-warps.db");
            File newFile = new File(this.getDataFolder(), "warps.db");
            if (old.exists() && !newFile.exists()) {
                XWarp.logger.info("No database found. Copying old database.");
                try {
                    MinecraftUtil.copy(old, newFile);
                } catch (IOException e) {
                    XWarp.logger.severe("Unable to copy database", e);
                }
            }
        }

        PluginProperties properties = new PluginProperties(this.getDataFolder(), this.getServer());
        
        this.dataConnection = properties.getDataConnection();
        try {
            if (!this.dataConnection.load(new File(this.getDataFolder(), this.dataConnection.getFilename()))) {
                XWarp.logger.severe("Could not load data. Disabling " + this.name + "!");
                this.getServer().getPluginManager().disablePlugin(this);
                return;
            }
        } catch (Exception e) {
            XWarp.logger.severe("Could not load data. Disabling " + this.name + "!", e);
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.permissionsWrapper = new PermissionsHandler(this.getServer().getPluginManager(), "", logger);
        permissions = this.permissionsWrapper;
        this.economyWrapper = new EconomyHandler(this.getServer().getPluginManager(), properties.getEconomyPlugin(), properties.getEconomyBaseAccount(), logger);
        
        WarpManager warpManager = new WarpManager(this, this.economyWrapper, properties, this.dataConnection);
        WPAManager wpaManager = new WPAManager(this, this.dataConnection);
        
        // Create commands
        WarpCommandMap wcm = null;
        WPACommandMap wpacm = null;
        try {
            wcm = new WarpCommandMap(warpManager, this.economyWrapper, this.getServer(), this.dataConnection, this.getDataFolder(), properties);
            wpacm = new WPACommandMap(wpaManager, this.economyWrapper, this.getServer(), this.dataConnection, this.getDataFolder(), properties);
        } catch (IllegalArgumentException iae) {
            XWarp.logger.severe("Couldn't initalize commands. Disabling " + this.name + "!", iae);
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.getCommand("go").setExecutor(wcm.getCommand(""));
        this.getCommand("warp").setExecutor(wcm);
        this.getCommand("wpa").setExecutor(wpacm);

        XWPlayerListener playerListener = new XWPlayerListener(warpManager, properties);
        XWBlockListener blockListener = new XWBlockListener(warpManager);
        ServerListener serverListner = new ServerListener() {
            @Override
            public void onPluginEnable(PluginEnableEvent event) {
                XWarp.this.permissionsWrapper.load(event.getPlugin());
                XWarp.this.economyWrapper.load(event.getPlugin());
            }

            @Override
            public void onPluginDisable(PluginDisableEvent event) {
                if (XWarp.this.permissionsWrapper.unload(event.getPlugin())) {
                    XWarp.this.permissionsWrapper.load();
                }
                if (XWarp.this.economyWrapper.unload(event.getPlugin())) {
                    XWarp.this.economyWrapper.load();
                }
            }
        };

        // Unless an event is called, to tell all enabled plugins
        this.permissionsWrapper.load();
        this.economyWrapper.load();
        
        this.getServer().getPluginManager().registerEvent(Event.Type.WORLD_LOAD, new XWWorldListener(warpManager), Priority.Low, this);
        try {
            this.getServer().getPluginManager().registerEvent(Event.Type.WORLD_UNLOAD, new XWWorldListener(warpManager), Priority.Low, this);
        } catch (NoSuchFieldError e) {
            // No unload on server: No problem at all. Since 834/835 there.
        }
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.ENTITY_DAMAGE, new XWEntityListener(properties, warpManager.getWarmUp()), Priority.Normal, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.SIGN_CHANGE, blockListener, Priority.Low, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_ENABLE, serverListner, Priority.Low, this);
        this.getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_DISABLE, serverListner, Priority.Low, this);
        XWarp.logger.info(name + " " + version + " enabled");
    }

    private void updateFiles() {
        File file = new File("MyWarp", "warps.db");
        File folder = new File("MyWarp");
        file.renameTo(new File(this.getDataFolder(), "warps.db"));
        folder.delete();
    }
}
