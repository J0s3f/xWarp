package de.xzise.xwarp;

import java.util.List;

import org.bukkit.World;
import org.bukkit.command.CommandSender;

import com.google.common.collect.ImmutableSet;

import de.xzise.xwarp.WarpManager.WarpObjectGetter;
import de.xzise.xwarp.dataconnections.DataConnection;
import de.xzise.xwarp.editors.EditorPermissions;
import de.xzise.xwarp.lister.options.Options;

public interface Manager<T extends WarpObject<?>> {

    void reload(DataConnection data);

    void delete(T warpObject, CommandSender sender);
    void setCreator(T warpObject, CommandSender sender, String creator);
    void setOwner(T warpObject, CommandSender sender, String owner);
    void setName(T warpObject, CommandSender sender, String name);

    void invite(T warpObject, CommandSender sender, String inviteeName);
    void uninvite(T warpObject, CommandSender sender, String inviteeName);

    void addEditor(T warpObject, CommandSender sender, String editor, EditorPermissions.Type type, String permissions);
    void removeEditor(T warpObject, CommandSender sender, String editor, EditorPermissions.Type type);

    boolean isNameAvailable(T warpObject);
    boolean isNameAvailable(String name, String owner);

    T getWarpObject(String name, String owner, String playerName);
    List<T> getWarpObjects(CommandSender sender, Options<?, T> options);
    ImmutableSet<T> getWarpObjects();
    int getSize();

    void missing(String name, String owner, CommandSender sender);

    void importWarpObjects(DataConnection connection, WarpObjectGetter<T> getter, CommandSender sender);

    int setWorld(World world);
    int unsetWorld(World world);

    void changeWorld(CommandSender sender, String oldWorld, String newWorld);
}