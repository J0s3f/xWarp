package de.xzise.xwarp;

import java.util.Comparator;

import org.bukkit.command.CommandSender;

import com.google.common.collect.ImmutableSet;

import de.xzise.StringComparator;
import de.xzise.xwarp.editors.Editor;
import de.xzise.xwarp.editors.EditorPermissions;

public interface WarpObject<T extends Editor> {

    String getName();
    String getOwner();
    String getCreator();
    String getWorld();
    String getType();
    boolean isListed(CommandSender sender);
    boolean canModify(CommandSender sender, T permission);
    boolean isValid();

    void addEditor(String name, EditorPermissions.Type type, ImmutableSet<T> permissions);
    void removeEditor(String name, EditorPermissions.Type type);

    T getInvitePermission();
    boolean hasPermission(String name, T permission);
    
    public static final Comparator<WarpObject<?>> WARP_OBJECT_NAME_COMPARATOR = new StringComparator<WarpObject<?>>() {

        @Override
        protected String getValue(WarpObject<?> warpObject) {
            return warpObject.getName();
        }

    };
}
