package me.taylorkelly.mywarp;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.xzise.xwarp.PermissionWrapper.PermissionTypes;


public class Warp {
	
	public enum Visibility {
		PRIVATE(0),
		PUBLIC(1),
		GLOBAL(2);
		
		public final int level;
		
		private Visibility(int level) {
			this.level = level;
		}
		
		public static Visibility parseLevel(int level) {
			for (Visibility visibility : Visibility.values()) {
				if (visibility.level == level) {
					return visibility;
				}
			}
			return null;
		}
	}
	
	public int index;
	public String name;
	public String creator;
	private Location location;
	public Visibility visibility;
	public String welcomeMessage;
	public List<String> permissions;
	public List<String> editors;

	public static int nextIndex = 1;

	public Warp(int index, String name, String creator, Location location, Visibility visibility,
			String permissions, String welcomeMessage) {
		this.index = index;
		this.name = name;
		this.creator = creator;
		this.location = location.clone();
		this.visibility = visibility;
		this.permissions = processList(permissions);
		this.editors = new ArrayList<String>();
		this.welcomeMessage = welcomeMessage;
		if (index > nextIndex)
			nextIndex = index;
		nextIndex++;
	}
	
	public Warp(String name, String creator, Location location) {
		this(nextIndex, name, creator, location, Visibility.PUBLIC, "", "Welcome to '" + name + "'");
	}

	public Warp(String name, Player creator) {
		this(name, creator.getName(), creator.getLocation());
	}

	public Warp(String name, Location location) {
		this(name, "No Player", location);
	}

	private static List<String> processList(String permissions) {
		String[] names = permissions.split(",");
		List<String> ret = new ArrayList<String>();
		for (String name : names) {
			if (name.equals(""))
				continue;
			ret.add(name.trim());
		}
		return ret;
	}

	public String permissionsString() {
		StringBuilder ret = new StringBuilder();
		for (String name : permissions) {
			ret.append(name);
			ret.append(",");
		}
		return ret.toString();
	}

	public boolean playerCanWarp(Player player, boolean viaSign) {	
		if (this.creator.equals(player.getName()) && MyWarp.permissions.permission(player, viaSign ? PermissionTypes.SIGN_WARP_OWN : PermissionTypes.TO_OWN))
			return true;
		if (this.permissions.contains(player.getName()) && MyWarp.permissions.permission(player, viaSign ? PermissionTypes.SIGN_WARP_INVITED : PermissionTypes.TO_INVITED))
			return true;
		if (this.visibility == Visibility.PUBLIC && MyWarp.permissions.permission(player, viaSign ? PermissionTypes.SIGN_WARP_OTHER : PermissionTypes.TO_OTHER))
			return true;
		if (this.visibility == Visibility.GLOBAL && MyWarp.permissions.permission(player, viaSign ? PermissionTypes.SIGN_WARP_GLOBAL : PermissionTypes.TO_GLOBAL))
			return true;
		return MyWarp.permissions.permission(player, PermissionTypes.ADMIN_TO_ALL);
	}
	
	public boolean playerCanWarp(Player player) {
		//TODO: More elegant version?
		return playerCanWarp(player, true) || playerCanWarp(player, false);
	}

	public void warp(Player player) {
		player.teleportTo(this.location);
	}

	public void update(Player player) {
		this.setLocation(player.getLocation());		
	}
	
	public void rename(String newName) {
		this.name = newName;
	}
	
	public boolean playerIsCreator(String name) {
		if (creator.equals(name))
			return true;
		return false;
	}

	public void invite(String player) {
		permissions.add(player);
	}

	public boolean playerIsInvited(String player) {
		return permissions.contains(player);
	}

	public void uninvite(String inviteeName) {
		permissions.remove(inviteeName);
	}

	public boolean playerCanModify(Player player) {
		if (creator.equals(player.getName()))
			return true;
		return false;
	}
	
	public boolean listWarp(CommandSender sender) {
		
		// Admin permissions
		if (MyWarp.permissions.hasAdminPermission(sender))
			return true;
		
		if (sender instanceof Player) {
			// Can warp
			if (this.playerCanWarp((Player) sender))
				return true;
			// Creator permissions
			if (this.playerIsCreator(((Player) sender).getName()))
				return true;
		}
			
		return false;
	}

	public void setCreator(String giveeName) {
		this.creator = giveeName;
	}
	
	public void setMessage(String message) {
		this.welcomeMessage = message;
	}
	
	public Location getLocation() {
		return this.location.clone();
	}
	
	public boolean isValid() {
		return this.location.getWorld() != null;
	}
	
	public void setLocation(Location location) {
		this.location = location.clone();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Warp) {
			Warp w = (Warp) o;
			return w.name.equals(this.name) && w.creator.equals(this.creator);
		} else {
			return false;
		}
	}
}
