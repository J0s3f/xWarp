package de.xzise.xwarp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.xzise.xwarp.wrappers.permission.PermissionTypes;

public enum Permissions implements PermissionDouble {
    UPDATE('l', 0, PermissionTypes.ADMIN_UPDATE, PermissionTypes.EDIT_LOCATION),
    RENAME('r', 1, PermissionTypes.ADMIN_RENAME, PermissionTypes.EDIT_RENAME),
    UNINVITE('u', 2, PermissionTypes.ADMIN_UNINVITE, PermissionTypes.EDIT_UNINVITE),
    INVITE('i', 3, PermissionTypes.ADMIN_INVITE, PermissionTypes.EDIT_INVITE),
    PRIVATE('0', 4, PermissionTypes.ADMIN_PRIVATE, PermissionTypes.CREATE_PRIVATE),
    PUBLIC('1', 5, PermissionTypes.ADMIN_PUBLIC, PermissionTypes.CREATE_PUBLIC),
    GLOBAL('2', 6, PermissionTypes.ADMIN_GLOBAL, PermissionTypes.CREATE_GLOBAL),
    GIVE('g', 7, PermissionTypes.ADMIN_CHANGE_OWNER, PermissionTypes.EDIT_CHANGE_OWNER),
    DELETE('d', 8, PermissionTypes.ADMIN_DELETE, PermissionTypes.EDIT_DELETE),
    WARP('w', 9, PermissionTypes.ADMIN_TO_ALL, null),
    ADD_EDITOR('a', 10, PermissionTypes.ADMIN_EDITORS_ADD, PermissionTypes.EDIT_EDITORS_ADD),
    REMOVE_EDITOR('f', 11, PermissionTypes.ADMIN_EDITORS_REMOVE, PermissionTypes.EDIT_EDITORS_REMOVE),
    MESSAGE('m', 12, PermissionTypes.ADMIN_MESSAGE, PermissionTypes.EDIT_MESSAGE),
    PRICE('p', 13, PermissionTypes.ADMIN_PRICE, PermissionTypes.EDIT_PRICE),
    FREE('c', 14, PermissionTypes.ADMIN_FREE, PermissionTypes.EDIT_FREE),
    LIST('s', 15, PermissionTypes.ADMIN_LIST_CHANGE, PermissionTypes.EDIT_LIST),

    ;

    public final char value;
    public final int id;
    public final PermissionTypes adminPermission;
    public final PermissionTypes defaultPermission;

    public static final Set<Permissions> DEFAULT;
    private static final Map<Character, Permissions> CHAR_MAP = new HashMap<Character, Permissions>();
    private static final Map<Integer, Permissions> INT_MAP = new HashMap<Integer, Permissions>();

    private Permissions(char value, int id, PermissionTypes adminPermission, PermissionTypes defaultPermission) {
        this.value = value;
        this.id = id;
        this.adminPermission = adminPermission;
        this.defaultPermission = defaultPermission;
    }

    static {
        DEFAULT = new HashSet<Permissions>();
        DEFAULT.add(UPDATE);
        DEFAULT.add(RENAME);
        DEFAULT.add(UNINVITE);
        DEFAULT.add(INVITE);
        DEFAULT.add(WARP);

        for (Permissions perm : Permissions.values()) {
            CHAR_MAP.put(perm.value, perm);
            INT_MAP.put(perm.id, perm);
        }
    }

    public static Permissions getById(int id) {
        return INT_MAP.get(id);
    }

    public static Permissions getByChar(char ch) {
        return CHAR_MAP.get(ch);
    }

    public static Set<Permissions> parseString(String permissions) {
        Set<Permissions> result = new HashSet<Permissions>();
        parseString(permissions, result);
        return result;
    }

    public static void parseString(String permissions, Set<Permissions> result) {
        result.clear();
        boolean remove = false;

        for (char c : permissions.toLowerCase().toCharArray()) {
            if (c == '/') {
                remove = true;
            } else if (c == '*') {
                if (remove) {
                    result.removeAll(Arrays.asList(Permissions.values()));
                } else {
                    result.addAll(Arrays.asList(Permissions.values()));
                }
            } else if (c == 's') {
                if (remove) {
                    result.removeAll(DEFAULT);
                } else {
                    result.addAll(DEFAULT);
                }
            } else {
                Permissions p = CHAR_MAP.get(c);
                if (p != null) {
                    if (remove) {
                        result.remove(p);
                    } else {
                        result.add(p);
                    }
                } else {
                    // TODO: How to react?
                }
            }
        }
    }

    @Override
    public PermissionTypes getDefault() {
        return this.defaultPermission;
    }

    @Override
    public PermissionTypes getAdmin() {
        return this.adminPermission;
    }
}
