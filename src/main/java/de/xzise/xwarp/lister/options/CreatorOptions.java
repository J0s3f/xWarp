package de.xzise.xwarp.lister.options;

import de.xzise.xwarp.WarpObject;

public class CreatorOptions<W extends WarpObject<?>> extends StringOptions<W> {

    @Override
    public String getString(W warpObject) {
        return warpObject.getCreator();
    }

}