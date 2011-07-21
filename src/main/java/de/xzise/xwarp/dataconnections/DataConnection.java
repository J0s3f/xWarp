package de.xzise.xwarp.dataconnections;

import java.io.File;
import java.util.List;

import de.xzise.xwarp.Warp;
import de.xzise.xwarp.editors.EditorPermissions;


public interface DataConnection {

    boolean load(File file);

    boolean create(File file);

    void free();

    String getFilename();

    void clear();

    /**
     * This method should be called to create an identification for a warp,
     * before any changes to this warp will be performed.
     * 
     * @param warp
     *            The identification for the warp will be created.
     * @return An identification.
     */
    IdentificationInterface<Warp> createIdentification(Warp warp);

    List<Warp> getWarps();

    void addWarp(Warp... warp);

    void deleteWarp(Warp warp);

    void updateCreator(Warp warp);

    void updateOwner(Warp warp, IdentificationInterface<Warp> identification);

    void updateName(Warp warp, IdentificationInterface<Warp> identification);

    void updateMessage(Warp warp);

    void updateVisibility(Warp warp);

    void updateLocation(Warp warp);

    void updateEditor(Warp warp, String name, EditorPermissions.Type type);

    void updatePrice(Warp warp);
}
