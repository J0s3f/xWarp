package me.taylorkelly.mywarp;

import java.io.File;
import java.io.IOException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
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
	
	private WMPlayerListener playerListener;
	private CommandMap commands;
	private DataConnection dataConnection;
	public String name;
	public String version;

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
		
		permissions.init(this.getServer());
		
		WarpManager warpList = new WarpManager(this.getServer(), this.dataConnection);

		// Create commands
		this.commands = null;
		try {
			this.commands = new CommandMap(warpList, this.getServer(), this.dataConnection);
		} catch (IllegalArgumentException iae) {
			MyWarp.logger.severe("Couldn't initalize commands.", iae);
			this.getServer().getPluginManager().disablePlugin(this);
		}
		
		this.playerListener = new WMPlayerListener(this.commands);
		MWBlockListener blockListener = new MWBlockListener(warpList);
		this.getServer().getPluginManager().registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);
		this.getServer().getPluginManager().registerEvent(Event.Type.BLOCK_RIGHTCLICKED, blockListener, Priority.Normal, this);
		this.getServer().getPluginManager().registerEvent(Event.Type.SIGN_CHANGE, blockListener, Priority.Low, this);
//		this.getServer().getPluginManager().registerEvent(Event.Type.BLOCK_CANBUILD, blockListener, Priority.Normal, this);
//		this.getServer().getPluginManager().registerEvent(Event.Type.BLOCK_PLACED, blockListener, Priority.Low, this);
		MyWarp.logger.info(name + " " + version + " enabled");
	}
	
	@Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {		
		return this.commands.executeCommand(sender, args);
    }

	private void updateFiles() {
		File file = new File("MyWarp", "warps.db");
		File folder = new File("MyWarp");
		file.renameTo(new File(this.getDataFolder(), "warps.db"));
		folder.delete();
	}
}
