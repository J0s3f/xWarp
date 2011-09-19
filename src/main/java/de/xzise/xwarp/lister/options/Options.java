package de.xzise.xwarp.lister.options;

import java.util.Set;

public interface Options<C, T> {

    Option get(String prefix);

    Set<C> getColumns();
    
    boolean isEmpty();
    
    boolean isMatched(T object);
    
    String[] getOwners();

}
