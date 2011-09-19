package de.xzise.xwarp.commands;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.xzise.xwarp.DefaultArrays;
import de.xzise.xwarp.WarpObject;
import de.xzise.xwarp.lister.GenericLister.Column;
import de.xzise.xwarp.lister.options.Option;
import de.xzise.xwarp.lister.options.Options;
import de.xzise.xwarp.lister.options.OwnerOptions;
import de.xzise.xwarp.lister.options.WarpObjectOptions;
import de.xzise.xwarp.lister.options.WhiteBlackList;

public class WarpOptions<W extends WarpObject<?>> implements Options<Column, W> {

    private final ImmutableMap<String, Option> options;
    private final ImmutableSet<WarpObjectOptions<?, W>> op;
    private final WhiteBlackList<Column, ? extends Set<Column>> columns;
    private final OwnerOptions<W> owners;
    
    public WarpOptions(Map<String, Option> optionMap, Set<WarpObjectOptions<?, W>> optionSet, OwnerOptions<W> ownerOption, WhiteBlackList<Column, ? extends Set<Column>> columns) {
        this.columns = columns;
        this.owners = ownerOption;
        this.op = ImmutableSet.copyOf(optionSet);
        this.options = ImmutableMap.copyOf(optionMap);
    }
    
    @Override
    public Option get(String prefix) {
        return this.options.get(prefix);
    }

    @Override
    public Set<Column> getColumns() {
        return this.columns.getWhitelist();
    }

    @Override
    public boolean isEmpty() {
        for (WarpObjectOptions<?, W> option : this.op) {
            if (!option.getWhitelist().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isMatched(W warpObject) {
        for (WarpObjectOptions<?, W> option : this.op) {
            Boolean b = option.call(warpObject);
            if (b != null && !b) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String[] getOwners() {
        return this.owners.getWhitelist().toArray(DefaultArrays.EMPTY_STRING_ARRAY);
    }

}
