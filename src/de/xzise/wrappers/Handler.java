package de.xzise.wrappers;

import java.util.Map;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;

import de.xzise.XLogger;

public class Handler<W extends Wrapper> {

    private final Map<String, ? extends Factory<W>> factories;
    private final PluginManager pluginManager;
    protected final XLogger logger;
    private final String type;
    private final W nullary;
    private String pluginName;
    private W wrapper;
    
    public Handler(Map<String, ? extends Factory<W>> factories, PluginManager pluginManager, String type, String plugin, XLogger logger) {
        this(factories, null, pluginManager, type, plugin, logger);
    }
    
    public Handler(Map<String, ? extends Factory<W>> factories, W nullaryWrapper, PluginManager pluginManager, String type, String plugin, XLogger logger) {
        this.factories = factories;
        this.pluginManager = pluginManager;
        this.logger = logger;
        this.type = type;
        this.nullary = nullaryWrapper;
        this.pluginName = plugin;
    }
    
    public void setPluginName(String name) {
        this.pluginName = name;
    }
    
    public boolean isActive() {
        return this.wrapper != null && this.wrapper != this.nullary;
    }
    
    public W getWrapper() {
        return this.wrapper == null ? this.nullary : this.wrapper;
    }
    
    public void load() {
        this.wrapper = null;
        for (String string : this.factories.keySet()) {
            this.load(this.pluginManager.getPlugin(string));
            if (this.wrapper != null) {
                return;
            }
        }
        if (this.wrapper == null) {
            if (this.pluginName == null) {
                this.logger.info("Loaded no " + this.type + " system, because it is deactivated.");
            } else {
                this.logger.info("No " + this.type + " system found until here. A " + this.type + " plugin will be maybe activated later.");
            }
        }
    }
    
    protected void loaded() {}
    
    public void load(Plugin plugin) {
        if (plugin != null && this.wrapper == null && this.pluginName != null) {
            PluginDescriptionFile pdf = plugin.getDescription();
            if (this.pluginName.isEmpty() || (pdf.getName().equalsIgnoreCase(this.pluginName))) {
                Factory<W> factory = factories.get(pdf.getName());
                if (factory != null) {
                    if (plugin.isEnabled()) {
                        try {
                            this.wrapper = factory.create(plugin, this.logger);
                        } catch (Exception e) {
                            //TODO: Better exception handling
                            this.wrapper = null;
                        }
                        if (this.wrapper == null) {
                            this.logger.warning("Invalid " + this.type + " system found: " + pdf.getFullName());
                        } else {
                            this.loaded();
                            this.logger.info("Linked with " + this.type + " system: " + pdf.getFullName());
                        }
                    } else {
                        this.logger.warning("Doesn't link to disabled " + this.type + " system: " + pdf.getFullName());
                    }
                }
            }
        }
    }
    
    public boolean unload(Plugin plugin) {
        if (this.wrapper != null && plugin == this.wrapper.getPlugin()) {
            this.wrapper = null;
            this.logger.info("Deactivated " + this.type + " system.");
            return true;
        } else {
            return false;
        }
    }
    
}
