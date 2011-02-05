package me.taylorkelly.mywarp;

import java.util.ArrayList;

import org.angelsl.minecraft.randomshit.fontwidth.MinecraftFontWidthCalculator;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Lister {
	private WarpList warpList;
	private Player player;
	
	private int maxPages;
	private int page;
	private String introRight;
	
	private static final int WARPS_PER_PAGE = 8;
	ArrayList<Warp> sortedWarps;
	
	private String creator;

	public static final ChatColor GLOBAL_OWN = ChatColor.AQUA;
	public static final ChatColor PUBLIC_OWN = ChatColor.BLUE;
	public static final ChatColor PRIVATE_OWN = ChatColor.DARK_BLUE;
	
	public static final ChatColor GLOBAL_OTHER = ChatColor.DARK_GREEN;
	public static final ChatColor PUBLIC_OTHER = ChatColor.GREEN;
	public static final ChatColor PRIVATE_OTHER = ChatColor.RED;
	
	public static final ChatColor PRIVATE_INVITED = ChatColor.YELLOW;

	public Lister(WarpList warpList) {
		this.warpList = warpList;
		this.maxPages = -1;
	}

	public void setPlayer(Player player) {
		this.player = player;
		this.maxPages = -1;
	}
	
	public void setCreator(String creator) {
		this.creator = creator;
		this.maxPages = -1;
	}
	
	public void calculateMaxPages() {
		int size = this.warpList.getSize(this.player, this.creator);
		this.maxPages = (int) Math.ceil(size / (double) WARPS_PER_PAGE);
		this.introRight = "/" + maxPages + " ";
		int width = 20 - this.getWidth(maxPages, 10);
		while (width > 0) {
			this.introRight += "-";
			width--;
		}
	}

	public void setPage(int page) {
		this.page = page;
		this.generatePage();
	}
	
	public void generatePage() {
		int start = (page-1)*WARPS_PER_PAGE;
		if (this.creator != null) {
			this.sortedWarps = this.warpList.getSortedWarps(this.player, this.creator, start, WARPS_PER_PAGE);
		} else {
			this.sortedWarps = this.warpList.getSortedWarps(player, start, WARPS_PER_PAGE);
		}
	}

	private int getWidth(int number, int base) {
		int width = 1;
		while (number >= base) {
			number /= base;
			width++;
		}
		return width;
	}
	
	public void list() {
		if (this.maxPages < 0)
			this.calculateMaxPages();
		
		// Generate header with the same length every time
		String intro = "";
		int width = 20 - this.getWidth(page, 10);
		while (width > 0) {
			intro += "-";
			width--;
		}
		intro += " Page " + page + introRight;
		
		this.player.sendMessage(ChatColor.YELLOW + intro);
		for(Warp warp: sortedWarps) {
			String name = warp.name;
			String creator = (warp.creator.equalsIgnoreCase(player.getName()))?"you":warp.creator;
			int x = (int) Math.round(warp.x);
			int y = (int) Math.round(warp.y);
			int z = (int) Math.round(warp.z);
			ChatColor color = getColor(warp, this.player);			
		
			String location = " @(" + x + ", " + y + ", " + z + ")";
			String creatorString = " by " + creator;
			
			//Find remaining length left
			int left = MinecraftFontWidthCalculator.getStringWidth(intro) - MinecraftFontWidthCalculator.getStringWidth("''" + creatorString + location);
			
			int nameLength = MinecraftFontWidthCalculator.getStringWidth(name);
			if(left > nameLength) {
				name = "'" + name + "'" + ChatColor.WHITE + creatorString  + whitespace(left - nameLength);
			} else if (left < nameLength) {
				name = "'" + substring(name, left) + "'" + ChatColor.WHITE + creatorString;
			}

			player.sendMessage(color + name + location);
		}
	}

	/**
	 * Lob shit off that string till it fits.
	 */
	private String substring(String name, int left) {
		while(MinecraftFontWidthCalculator.getStringWidth(name) > left) {
			name = name.substring(0, name.length()-1);
		}
		return name;
	}

	public int getMaxPages() {
		if (this.maxPages < 0) {
			this.calculateMaxPages();
		}
		return this.maxPages;
	}
	
	public String whitespace(int length) {
		int spaceWidth = MinecraftFontWidthCalculator.getStringWidth(" ");
		
		StringBuilder ret = new StringBuilder();
		
		for(int i = 0; i < length; i+=spaceWidth) {
			ret.append(" ");
		}
		
		return ret.toString();
	}
	
	public static String[] getLegend() {
		String[] result = new String[6];
		result[0] = ChatColor.RED + "-------------------- " + ChatColor.WHITE + "LIST LEGEND" + ChatColor.RED
		+ " -------------------";
		int i = 1;
//		result[i++] = GLOBAL_OWN + "Yours and it is global";
		result[i++] = PUBLIC_OWN + "Yours and it is public.";
		result[i++] = PRIVATE_OWN + "Yours and it is private.";
//		result[i++] = GLOBAL_OTHER + "Not yours and it is global";
		result[i++] = PUBLIC_OTHER + "Not yours and it is public";
		result[i++] = PRIVATE_OTHER + "Not yours, private and not invited";
		result[i++] = PRIVATE_INVITED + "Not yours, private and you are invited";
		return result;
	}
	
	public static ChatColor getColor(Warp warp, Player player) {
		if(warp.playerIsCreator(player.getName())) {
			switch (warp.visibility) {
			case PRIVATE :
				return PRIVATE_OWN;
			case PUBLIC :
				return PUBLIC_OWN;
			case GLOBAL :
				return GLOBAL_OWN;
			}
		} else {
			switch (warp.visibility) {
			case PRIVATE :
				if (warp.playerCanWarp(player)) {
					return PRIVATE_INVITED;
				} else {
					return PRIVATE_OTHER;
				}
			case PUBLIC :
				return PUBLIC_OTHER;
			case GLOBAL :
				return GLOBAL_OTHER;
			}
		}
		return PRIVATE_OTHER;
	}
}
