package de.xzise.xwarp.lister.options;

import java.util.HashSet;
import java.util.Set;


public abstract class DefWhiteBlackList<V> extends WhiteBlackList<V, Set<V>> {
    public DefWhiteBlackList(Set<V> white, Set<V> black) {
        super(white, black);
    }

    public DefWhiteBlackList() {
        this(new HashSet<V>(), new HashSet<V>());
    }
}