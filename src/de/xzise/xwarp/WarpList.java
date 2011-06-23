package de.xzise.xwarp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;

import de.xzise.MinecraftUtil;

import me.taylorkelly.mywarp.Warp;
import me.taylorkelly.mywarp.Warp.Visibility;

public class WarpList {

    private class GlobalMap {

        private Warp global;
        private final Map<String, Warp> all;

        public GlobalMap() {
            this.global = null;
            this.all = new HashMap<String, Warp>();
        }

        public void put(Warp warp) {
            if (warp.visibility == Visibility.GLOBAL) {
                this.global = warp;
            }
            this.all.put(warp.getCreator().toLowerCase(), warp);
        }

        public void delete(Warp warp) {
            if (warp.equals(this.global)) {
                this.global = null;
            }
            this.all.remove(warp.getCreator().toLowerCase());
        }

        public Warp getWarp(String playerName) {
            if (this.global != null) {
                return this.global;
            }

            if (this.all.size() == 1) {
                return this.all.values().toArray(new Warp[1])[0];
            } else if (playerName != null && !playerName.isEmpty()) {
                return this.all.get(playerName.toLowerCase());
            } else {
                return null;
            }
        }

        public void updateGlobal(Warp warp) {
            if (this.global == null && warp.visibility == Visibility.GLOBAL) {
                this.global = warp;
            } else if (warp.equals(this.global) && warp.visibility != Visibility.GLOBAL) {
                this.global = null;
            }
        }

        public void clear() {
            this.all.clear();
            this.global = null;
        }
    }

    // Warps sorted by owner, name
    private final Map<String, Map<String, Warp>> personal;
    // Warps sorted by creator
    private final Map<String, List<Warp>> creatorMap;
    // Warps sorted by name
    private final Map<String, GlobalMap> global;

    public WarpList() {
        this.personal = new HashMap<String, Map<String, Warp>>();
        this.global = new HashMap<String, GlobalMap>();
        this.creatorMap = new HashMap<String, List<Warp>>();
    }

    public void loadList(Collection<Warp> warps) {
        for (Map<String, Warp> personalWarps : this.personal.values()) {
            personalWarps.clear();
        }
        for (List<Warp> creatorWarps : this.creatorMap.values()) {
            creatorWarps.clear();
        }
        for (GlobalMap globalWarps : this.global.values()) {
            globalWarps.clear();
        }

        // Load elements here
        for (Warp warp : warps) {
            this.addWarp(warp);
        }
    }

    public void addWarp(Warp warp) {
        GlobalMap namedWarps = this.global.get(warp.name.toLowerCase());
        if (namedWarps == null) {
            namedWarps = new GlobalMap();
            this.global.put(warp.name.toLowerCase(), namedWarps);
        }
        namedWarps.put(warp);

        Map<String, Warp> personalWarps = this.personal.get(warp.getOwner().toLowerCase());
        if (personalWarps == null) {
            personalWarps = new HashMap<String, Warp>();
            this.personal.put(warp.getOwner().toLowerCase(), personalWarps);
        }
        personalWarps.put(warp.name.toLowerCase(), warp);

        if (MinecraftUtil.isSet(warp.getCreator())) {
            List<Warp> creatorWarps = this.creatorMap.get(warp.getCreator().toLowerCase());
            if (creatorWarps == null) {
                creatorWarps = new ArrayList<Warp>();
                this.creatorMap.put(warp.getCreator().toLowerCase(), creatorWarps);
            }
            creatorWarps.add(warp);
        }
    }

    public void deleteWarp(Warp warp) {
        this.global.get(warp.name.toLowerCase()).delete(warp);
        if (MinecraftUtil.isSet(warp.getCreator())) {
            this.creatorMap.get(warp.getCreator().toLowerCase()).remove(warp);
        }
        this.personal.get(warp.getOwner().toLowerCase()).remove(warp.name.toLowerCase());
    }

    public void updateOwner(Warp warp, String preOwner) {
        this.personal.get(preOwner.toLowerCase()).remove(warp.name.toLowerCase());
        Map<String, Warp> personalWarps = this.personal.get(warp.getOwner().toLowerCase());
        if (personalWarps == null) {
            personalWarps = new HashMap<String, Warp>();
            this.personal.put(warp.getOwner().toLowerCase(), personalWarps);
        }
        personalWarps.put(warp.name.toLowerCase(), warp);
    }

    public void updateVisibility(Warp warp) {
        this.global.get(warp.name.toLowerCase()).updateGlobal(warp);
    }

    /**
     * Returns the number of warps a player has created.
     * 
     * @param creator
     *            The creator of the warps. Has to be not null.
     * @param visibility
     *            The visibility of the warps. Set to null if want to show all
     *            visibilities.
     * @param world
     *            The world the warps has to be in. If null, it checks all
     *            worlds.
     * @return The number of warps the player has created (with the desired
     *         visibility).
     */
    public int getNumberOfWarps(String creator, Visibility visibility, String world) {
        int number = 0;
        if (MinecraftUtil.isSet(creator)) {
            List<Warp> warps = this.creatorMap.get(creator.toLowerCase());
            if (warps != null) {
                for (Warp warp : warps) {
                    if ((visibility == null || warp.visibility == visibility) && (world == null || warp.getLocationWrapper().getWorld().equals(world))) {
                        number++;
                    }
                }
            }
        }
        return number;
    }

    public Warp getWarp(String name, String owner, String playerName) {
        if (owner == null || owner.isEmpty()) {
            GlobalMap namedWarps = this.global.get(name.toLowerCase());
            if (namedWarps != null) {
                return namedWarps.getWarp(playerName);
            } else {
                return null;
            }
        } else {
            Map<String, Warp> ownerWarps = this.personal.get(owner.toLowerCase());
            if (ownerWarps != null) {
                return ownerWarps.get(name.toLowerCase());
            }
            return null;
        }
    }

    public Warp getWarp(String name) {
        return this.getWarp(name, null, null);
    }

    public List<Warp> getWarps() {
        List<Warp> result = new ArrayList<Warp>();
        for (Map<String, Warp> map : this.personal.values()) {
            result.addAll(map.values());
        }
        return result;
    }

    public List<Warp> getWarps(String owner) {
        Map<String, Warp> personalWarps = this.personal.get(owner.toLowerCase());
        if (personalWarps != null) {
            return new ArrayList<Warp>(personalWarps.values());
        } else {
            return new ArrayList<Warp>(0);
        }
    }

    /**
     * Returns the number of warps the player can modify/use.
     * 
     * @param player
     *            The given player.
     * @return The number of warps the player can modify/use.
     */
    public int getSize(CommandSender sender) {
        int size = 0;
        for (Map<String, Warp> map : this.personal.values()) {
            size += this.getSize(sender, map);
        }
        return size;
    }

    public int getSize(CommandSender sender, String creator) {
        if (creator == null || creator.isEmpty()) {
            return this.getSize(sender);
        } else {
            Map<String, Warp> map = this.personal.get(creator.toLowerCase());
            return map == null ? 0 : this.getSize(sender, map);
        }
    }

    private int getSize(CommandSender sender, Map<String, Warp> map) {
        if (sender == null) {
            return map.size();
        } else {
            int size = 0;
            for (Warp warp : map.values()) {
                if (warp.listWarp(sender)) {
                    size++;
                }
            }
            return size;
        }
    }

}
