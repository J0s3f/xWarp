package de.xzise.xwarp;

import org.bukkit.command.CommandSender;

import de.xzise.metainterfaces.FixedLocation;
import de.xzise.xwarp.editors.WarpProtectionAreaPermissions;
import de.xzise.xwarp.warpable.Positionable;
import de.xzise.xwarp.wrappers.permission.WPAPermissions;

public class WarpProtectionArea extends DefaultWarpObject<WarpProtectionAreaPermissions> {

    private int index;
    private final WorldWrapper world;
    private final FixedLocation firstCorner;
    private final FixedLocation secondCorner;
    
    public static int nextIndex = 1;
    
    public WarpProtectionArea(WorldWrapper world, FixedLocation firstCorner, FixedLocation secondCorner, String name, String owner, String creator) {
        this(nextIndex, world, firstCorner, secondCorner, name, owner, creator);
    }
    
    public WarpProtectionArea(int index, WorldWrapper world, FixedLocation firstCorner, FixedLocation secondCorner, String name, String owner, String creator) {
        //TODO: null as editor?!
        super(name, owner, creator, null, WarpProtectionAreaPermissions.class, WarpProtectionAreaPermissions.OVERWRITE);
        this.index = index;
        this.world = world;
        this.firstCorner = firstCorner;
        this.secondCorner = secondCorner;
        if (index > nextIndex)
            nextIndex = index;
        nextIndex++;
    }
    
    public boolean isWithIn(Positionable positionable) {
        return this.isWithIn(new FixedLocation(positionable.getLocation()));
    }

    public void assignNewId() {
        this.index = nextIndex++;
    }

    public boolean isWithIn(FixedLocation location) {
        if (this.isValid() && location.world.equals(world.getWorld())) {
            double lowerX = Math.min(this.firstCorner.x, this.secondCorner.x);
            double upperX = Math.max(this.firstCorner.x, this.secondCorner.x);
            double lowerY = Math.min(this.firstCorner.y, this.secondCorner.y);
            double upperY = Math.max(this.firstCorner.y, this.secondCorner.y);
            double lowerZ = Math.min(this.firstCorner.z, this.secondCorner.z);
            double upperZ = Math.max(this.firstCorner.z, this.secondCorner.z);
            double x = location.x;
            double y = location.y;
            double z = location.z;
            return lowerX <= x && x <= upperX && lowerY <= y && y <= upperY && lowerZ <= z && z <= upperZ;
        } else {
            return false;
        }
    }
    
    public static boolean isWithIn(final FixedLocation firstCorner, final FixedLocation secondCorner, final FixedLocation testLocation) {
        if (testLocation.world.equals(firstCorner.world) && firstCorner.world.equals(secondCorner.world)) {
            double lowerX = Math.min(firstCorner.x, secondCorner.x);
            double upperX = Math.max(firstCorner.x, secondCorner.x);
            double lowerY = Math.min(firstCorner.y, secondCorner.y);
            double upperY = Math.max(firstCorner.y, secondCorner.y);
            double lowerZ = Math.min(firstCorner.z, secondCorner.z);
            double upperZ = Math.max(firstCorner.z, secondCorner.z);
            double x = testLocation.x;
            double y = testLocation.y;
            double z = testLocation.z;
            return lowerX <= x && x <= upperX && lowerY <= y && y <= upperY && lowerZ <= z && z <= upperZ;
        } else {
            return false;
        } 
    }

    public int getId() {
        return this.index;
    }

    public boolean isAllowed(String name) {
        if (name.equals(this.getOwner())) {
            return true;
        } else {
            return this.hasPermission(name, WarpProtectionAreaPermissions.OVERWRITE);
        }
    }

    @Override
    public String getWorld() {
        return this.world.getWorldName();
    }

    public boolean isListed() {
        return true; //TODO: Add listed support.
    }

    @Override
    public boolean isListed(CommandSender sender) {
        if (!this.isListed() && !XWarp.permissions.permission(sender, WPAPermissions.ADMIN_LIST_VIEW)) {
            return false;
        }

        return true;
    }

    public boolean isValid() {
        return this.world.isValid();
    }

    public FixedLocation getCorner(int index) {
        switch (index) {
        case 0 :
            return this.firstCorner;
        case 1 :
            return this.secondCorner;
        default :
            return null;
        }
    }

    @Override
    public String getType() {
        return "wpa";
    }

    public WorldWrapper getWorldWrapper() {
        return this.world;
    }
}
