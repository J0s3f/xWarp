package me.taylorkelly.mywarp;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.server.PluginEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;

import de.xzise.MinecraftUtil;
import de.xzise.XLogger;
import de.xzise.xwarp.CommandMap;
import de.xzise.xwarp.PermissionWrapper;
import de.xzise.xwarp.PluginProperties;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.dataconnections.DataConnection;
import de.xzise.xwarp.dataconnections.SQLiteConnection;

public class MyWarp extends JavaPlugin {
	
	public static PermissionWrapper permissions = new PermissionWrapper();
	public static XLogger logger;
	
	private CommandMap commands;
	private DataConnection dataConnection;

	public String name;
	public String version;
	
	public MyWarp(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File directory, File plugin, ClassLoader cLoader) {
		super(pluginLoader, instance, desc, directory, plugin, cLoader);
		
		// Naging strike back!
		try {
			if (JavaPlugin.class.getConstructor() != null) {
				Logger.getLogger("Minecraft").info("[xWarp]: Temporary: Don't nag xZise/tkelly about the warning that it's using the wrong constructor: Bukkit calls the wrong constructor.");	
			}
		} catch (Exception e) {
			; // Do nothing here, if there is a problem, wayne :D
		}
	}

	public MyWarp() {
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
		logger = new XLogger(this.name);

		if (!this.getDataFolder().exists()) {
			this.getDataFolder().mkdir();
		}
		
		if(new File("MyWarp").exists() && new File("MyWarp", "warps.db").exists()) {
			this.updateFiles();
		} else {
			File old = new File("homes-warps.db"); 
			File newFile = new File(this.getDataFolder(), "warps.db");
			if (old.exists() && !newFile.exists()) {
				MyWarp.logger.info("No database found. Copying old database.");
				try {
					MinecraftUtil.copy(old, newFile);
				} catch (IOException e) {
					MyWarp.logger.severe("Unable to copy database", e);
				}
			}
		}
		
		PluginProperties properties = new PluginProperties(this.getDataFolder(), this.getServer());
		
		this.dataConnection = properties.getDataConnection();

		if (this.dataConnection instanceof SQLiteConnection) {
			try {
				((SQLiteConnection) this.dataConnection).init(this.getDataFolder());
			} catch (IllegalArgumentException iae) {
				MyWarp.logger.severe("Could not establish SQL connection. Disabling " + this.name + "!", iae);
				this.getServer().getPluginManager().disablePlugin(this);
				return;
			}
		}
		
		WarpManager warpList = new WarpManager(this.getServer(), this.dataConnection);

		// Create commands
		this.commands = null;
		try {
			this.commands = new CommandMap(warpList, this.getServer(), this.dataConnection);
		} catch (IllegalArgumentException iae) {
			MyWarp.logger.severe("Couldn't initalize commands.", iae);
			this.getServer().getPluginManager().disablePlugin(this);
		}
		
		PlayerListener playerListener = new WMPlayerListener(this.commands);
		MWBlockListener blockListener = new MWBlockListener(warpList);
		ServerListener serverListner = new ServerListener() {
			public void onPluginEnabled(PluginEvent event) {
				if(event.getPlugin().getDescription().getName().equals("Permissions")) {
					MyWarp.permissions.init(event.getPlugin());
				}
		    }
			
			public void onPluginDisabled(PluginEvent event) {
				if(event.getPlugin().getDescription().getName().equals("Permissions")) {
					MyWarp.permissions.init(null);
				}
		    }
		};
		
		try {
			this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);
		} catch (NoSuchFieldError nsfe) {
			try {
				this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Normal, this);
			} catch (NoSuchFieldError nsfe2) {
				MyWarp.logger.warning("Unable to register any player command. Only xWarp available");
			}
		}
		this.getServer().getPluginManager().registerEvent(Event.Type.BLOCK_RIGHTCLICKED, blockListener, Priority.Normal, this);
		this.getServer().getPluginManager().registerEvent(Event.Type.SIGN_CHANGE, blockListener, Priority.Low, this);
		this.getServer().getPluginManager().registerEvent(Event.Type.PLUGIN_ENABLE, serverListner, Priority.Low, this);
//		this.getServer().getPluginManager().registerEvent(Event.Type.BLOCK_CANBUILD, blockListener, Priority.Normal, this);
//		this.getServer().getPluginManager().registerEvent(Event.Type.BLOCK_PLACED, blockListener, Priority.Low, this);
		MyWarp.logger.info(name + " " + version + " enabled");
	}
	
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		StringBuilder line = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			line.append(arg);
			if (i < args.length - 1) {
				line.append(' ');
			}
		}
		
		return this.commands.executeCommand(sender, WMPlayerListener.parseLine(line.toString()));
    }

	private void updateFiles() {
		File file = new File("MyWarp", "warps.db");
		File folder = new File("MyWarp");
		file.renameTo(new File(this.getDataFolder(), "warps.db"));
		folder.delete();
	}
}
