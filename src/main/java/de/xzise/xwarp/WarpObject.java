package de.xzise.xwarp;

import org.bukkit.command.CommandSender;

import com.google.common.collect.ImmutableSet;

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
}
