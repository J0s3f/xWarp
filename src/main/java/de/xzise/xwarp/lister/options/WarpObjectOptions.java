package de.xzise.xwarp.lister.options;

import de.xzise.xwarp.WarpObject;

public abstract class WarpObjectOptions<V, W extends WarpObject<?>> extends DefWhiteBlackList<V> implements Option {

    public abstract V getValue(W warp);

    public Boolean call(W warp) {
        V value = getValue(warp);
        return super.call(value);
    }
}