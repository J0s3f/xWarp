package de.xzise.xwarp.wrappers.economy;

import me.taylorkelly.mywarp.MyWarp;

import org.bukkit.plugin.Plugin;

import com.earth2me.essentials.api.Economy;

public class Essentials implements EconomyWrapper {

    private final Plugin economy;
    
    public final class EssentialsAccount implements AccountWrapper {
        
        private final String name;
        
        public EssentialsAccount(String name) {
            this.name = name;
        }

        @Override
        public boolean hasEnough(int price) {
            return Economy.hasEnough(this.name, price);
        }

        @Override
        public void add(int price) {
            Economy.add(this.name, price);
        }
        
    }
    
    public Essentials(Plugin plugin) {
        this.economy = plugin;
    }
    
    @Override
    public AccountWrapper getAccount(String name) {
        return new EssentialsAccount(name);
    }

    @Override
    public String format(int price) {
        return Economy.format(price);
    }

    @Override
    public Plugin getPlugin() {
        return this.economy;
    }
    
    public static class Factory implements EconomyWrapperFactory {

        @Override
        public EconomyWrapper create(Plugin plugin) {
            if (plugin instanceof com.earth2me.essentials.Essentials) {
                Essentials buf = new Essentials(plugin);
                try {
                    buf.format(0);
                    return buf;
                } catch (NoClassDefFoundError e) {
                    MyWarp.logger.info("Essentials plugin found, but without Economy API. Should be there since Essentials 2.2.13");
                    return null;
                }
            } else {
                return null;
            }
        }
        
    }

}
