package de.xzise.xwarp.commands.warp;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Server;

import de.xzise.xwarp.Manager;
import de.xzise.xwarp.PluginProperties;
import de.xzise.xwarp.Warp;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.commands.WarpOptions;
import de.xzise.xwarp.lister.GenericLister;
import de.xzise.xwarp.lister.GenericLister.Column;
import de.xzise.xwarp.lister.WarpListSection;
import de.xzise.xwarp.lister.options.CreatorOptions;
import de.xzise.xwarp.lister.options.OfflineOption;
import de.xzise.xwarp.lister.options.Option;
import de.xzise.xwarp.lister.options.Options;
import de.xzise.xwarp.lister.options.OwnerOptions;
import de.xzise.xwarp.lister.options.VisibilityOptions;
import de.xzise.xwarp.lister.options.WarpObjectOptions;
import de.xzise.xwarp.lister.options.WorldOptions;
import de.xzise.xwarp.lister.ListSection;
import de.xzise.xwarp.wrappers.permission.PermissionTypes;

public class ListCommand extends de.xzise.xwarp.commands.ListCommand<Warp, Manager<Warp>, Column> {

    private final PluginProperties properties;

    public ListCommand(WarpManager manager, Server server, PluginProperties properties) {
        super(manager, server, PermissionTypes.CMD_LIST, "warp");
        this.properties = properties;
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Shows a list of warps. Following filters are available:", "oo:<owner>, o:<exp. owner>, oc:<creator>, c:<exp. creator>", "w:<world>, v:<visibility>, -col:{owner,world,location}", "Example: /warp list o:xZise -col:owner" };
    }

    @Override
    protected String[] getLegend() {
        return GenericLister.WarpLegend;
    }

    @Override
    protected Options<Column, Warp> getOptions() {
        Set<WarpObjectOptions<?, Warp>> optionSet = new HashSet<WarpObjectOptions<?,Warp>>(4);
        Map<String, Option> optionMap = new HashMap<String, Option>(7);
        
        CreatorOptions<Warp> creatorOptions = new CreatorOptions<Warp>();
        optionMap.put("c", creatorOptions);
        optionMap.put("oc", new OfflineOption(creatorOptions));
        optionSet.add(creatorOptions);
        
        OwnerOptions<Warp> ownerOptions = new OwnerOptions<Warp>();
        optionMap.put("o", ownerOptions);
        optionMap.put("oo", new OfflineOption(ownerOptions));
        optionSet.add(ownerOptions);
        
        WorldOptions<Warp> worldOptions = new WorldOptions<Warp>();
        optionMap.put("w", worldOptions);
        optionSet.add(worldOptions);
        
        VisibilityOptions visibilityOptions = new VisibilityOptions();
        optionMap.put("v", visibilityOptions);
        optionSet.add(visibilityOptions);
        
        WarpColumnOptions columnOptions = new WarpColumnOptions();
        optionMap.put("col", columnOptions);
        
        return new WarpOptions<Warp>(optionMap, optionSet, ownerOptions, columnOptions);
    }

    @Override
    protected ListSection<Warp> createListSection(int numLines, Set<Column> columns) {
        return new WarpListSection("", numLines, columns);
    }
}
