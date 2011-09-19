package de.xzise.xwarp.lister.options;

import java.util.EnumSet;

public class EnumWhiteBlackList<E extends Enum<E>> extends WhiteBlackList<E, EnumSet<E>> {

    protected final Class<E> enumClass;

    public EnumWhiteBlackList(Class<E> enumClass) {
        super(EnumSet.noneOf(enumClass), EnumSet.noneOf(enumClass));
        this.enumClass = enumClass;
    }

    /**
     * Returns all white listed and not defined elements.
     * 
     * @return all white listed and not defined elements.
     */
    public EnumSet<E> getSelected() {
        EnumSet<E> result = EnumSet.copyOf(this.getWhitelist());
        result.addAll(EnumSet.complementOf(this.getBlacklist()));
        return result;
    }

    public EnumSet<E> getByStatus(Boolean status) {
        EnumSet<E> result = EnumSet.noneOf(this.enumClass);
        for (E e : enumClass.getEnumConstants()) {
            if (this.call(e) == status) {
                result.add(e);
            }
        }
        return result;
    }
}