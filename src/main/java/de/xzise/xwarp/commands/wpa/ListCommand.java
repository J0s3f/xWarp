package de.xzise.xwarp.commands.wpa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Server;

import de.xzise.xwarp.Manager;
import de.xzise.xwarp.PluginProperties;
import de.xzise.xwarp.WarpProtectionArea;
import de.xzise.xwarp.commands.WarpOptions;
import de.xzise.xwarp.lister.GenericLister;
import de.xzise.xwarp.lister.ListSection;
import de.xzise.xwarp.lister.WPAListSection;
import de.xzise.xwarp.lister.GenericLister.Column;
import de.xzise.xwarp.lister.options.CreatorOptions;
import de.xzise.xwarp.lister.options.OfflineOption;
import de.xzise.xwarp.lister.options.Option;
import de.xzise.xwarp.lister.options.Options;
import de.xzise.xwarp.lister.options.OwnerOptions;
import de.xzise.xwarp.lister.options.WarpObjectOptions;
import de.xzise.xwarp.lister.options.WorldOptions;
import de.xzise.xwarp.wrappers.permission.WPAPermissions;

public class ListCommand extends de.xzise.xwarp.commands.ListCommand<WarpProtectionArea, Manager<WarpProtectionArea>, Column> {

    private final PluginProperties properties;

    public ListCommand(Manager<WarpProtectionArea> manager, Server server, PluginProperties properties) {
        super(manager, server, WPAPermissions.CMD_LIST, "wpa");
        this.properties = properties;
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Shows a list of protection areas. Following filters are available:", "oo:<exact owner>, o:<owner>, oc:<creator>, c:<exact creator>", "w:<world>, col:{owner,world,location}", "Example: /wpa list o:xZise -col:owner" };
    }

    @Override
    protected Options<Column, WarpProtectionArea> getOptions() {
        Set<WarpObjectOptions<?, WarpProtectionArea>> optionSet = new HashSet<WarpObjectOptions<?,WarpProtectionArea>>(3);
        Map<String, Option> optionMap = new HashMap<String, Option>(6);
        
        CreatorOptions<WarpProtectionArea> creatorOptions = new CreatorOptions<WarpProtectionArea>();
        optionMap.put("c", creatorOptions);
        optionMap.put("oc", new OfflineOption(creatorOptions));
        optionSet.add(creatorOptions);
        
        OwnerOptions<WarpProtectionArea> ownerOptions = new OwnerOptions<WarpProtectionArea>();
        optionMap.put("o", ownerOptions);
        optionMap.put("oo", new OfflineOption(ownerOptions));
        optionSet.add(ownerOptions);
        
        WorldOptions<WarpProtectionArea> worldOptions = new WorldOptions<WarpProtectionArea>();
        optionMap.put("w", worldOptions);
        optionSet.add(worldOptions);
        
        WarpColumnOptions columnOptions = new WarpColumnOptions();
        optionMap.put("col", columnOptions);
        
        return new WarpOptions<WarpProtectionArea>(optionMap, optionSet, ownerOptions, columnOptions);
    }

    @Override
    protected ListSection<WarpProtectionArea> createListSection(int numLines, Set<Column> columns) {
        return new WPAListSection("", numLines, columns);
    }

    @Override
    protected String[] getLegend() {
        return GenericLister.WPALegend;
    }
}
