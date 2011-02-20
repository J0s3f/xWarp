package me.taylorkelly.mywarp;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;

import de.xzise.MinecraftUtil;
import de.xzise.XLogger;
import de.xzise.xwarp.CommandMap;
import de.xzise.xwarp.PermissionWrapper;
import de.xzise.xwarp.dataconnections.DataConnection;
import de.xzise.xwarp.dataconnections.SQLiteConnection;

public class MyWarp extends JavaPlugin {
	
	public static PermissionWrapper permissions = new PermissionWrapper();
	public static XLogger logger;
	
	private WMPlayerListener playerListener;
	private CommandMap commands;
	private DataConnection dataConnection;
	public final String name = this.getDescription().getName();
	public final String version = this.getDescription().getVersion();
	
	public MyWarp(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File directory, File plugin, ClassLoader cLoader) {
		super(pluginLoader, instance, desc, directory, plugin, cLoader);
		Logger.getLogger("Minecraft").info("[xWarp]: Temporary: this naging message is ****... Tell bukkit dev team that they don't use the newer method if the older exists.");
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
		logger = new XLogger(this.name);

		if(new File("MyWarp").exists() && new File("MyWarp", "warps.db").exists()) {
			updateFiles();
		}
		
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
		
		// Init connection here
		try {
			this.dataConnection = new SQLiteConnection(this.getServer(), this.getDataFolder());
		} catch (Exception e) {
			MyWarp.logger.severe("Could not establish SQL connection. Disabling " + name + "!");
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		permissions.init(this.getServer());
		
		WarpList warpList = new WarpList(this.getServer(), this.dataConnection);

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
