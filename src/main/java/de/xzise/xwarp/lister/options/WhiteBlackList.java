package de.xzise.xwarp.lister.options;

import java.util.Set;

import de.xzise.Callback;

public class WhiteBlackList<V, S extends Set<V>> implements Callback<Boolean, V> {
    private final S white, black;

    public WhiteBlackList(S white, S black) {
        this.white = white;
        this.black = black;
    }

    public boolean add(V value, boolean white) {
        if (white) {
            return this.white.add(value);
        } else {
            return this.black.add(value);
        }
    }

    public S getWhitelist() {
        return this.white;
    }

    public S getBlacklist() {
        return this.black;
    }

    public boolean isEmpty() {
        return this.white.isEmpty() && this.black.isEmpty();
    }

    @Override
    public Boolean call(V value) {
        if (this.black.contains(value)) {
            return false;
        } else if (this.white.contains(value)) {
            return true;
        } else if (this.isEmpty()) {
            return null;
        } else {
            return this.white.isEmpty();
        }
    }
}