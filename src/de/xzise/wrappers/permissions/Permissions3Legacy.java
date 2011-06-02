package de.xzise.wrappers.permissions;

import java.util.LinkedHashSet;

import com.nijiko.permissions.Entry;
import com.nijiko.permissions.Group;
import com.nijiko.permissions.PermissionHandler;
import com.nijiko.permissions.User;
import com.nijiko.permissions.Entry.EntryVisitor;

public class Permissions3Legacy {
    
    /**
     * A visitor class, to get the raw integer value.
     * 
     * Maybe exposed soon in Permissions 3. Until then use this one.
     */
    public static final class IntVisitor implements EntryVisitor<Integer> {

        private final String name;
        
        public IntVisitor(final String name) {
            this.name = name;
        }
        
        @Override
        public Integer value(Entry entry) {
            return entry.getRawInt(this.name);
        }
        
    }

    /**
     * A visitor class, to get the raw double value.
     * 
     * Maybe exposed soon in Permissions 3. Until then use this one.
     */
    public static final class DoubleVisitor implements EntryVisitor<Double> {

        private final String name;
        
        public DoubleVisitor(final String name) {
            this.name = name;
        }
        
        @Override
        public Double value(Entry entry) {
            return entry.getRawDouble(this.name);
        }
        
    }

    /*
     * Copied code from the 3.0.2 source code.
     */
    public static String getPrimaryGroup(String world, String user, PermissionHandler handler) {
        User u = handler.getUserObject(world, user);
        if(u != null) {
            LinkedHashSet<Entry> parents = u.getParents();
            if(parents != null && !parents.isEmpty()) {
                for(Entry e : parents) {
                    if(e instanceof Group)
                        return e.getName();
                }
            }
        }
        Group def = handler.getDefaultGroup(world);
        if(def != null) return def.getName();
        return "Default";
    }
}