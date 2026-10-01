package it.maurof00.xeonkitpvp.hook;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class VaultHook {
    private final JavaPlugin plugin;
    private Economy economy;

    public VaultHook(JavaPlugin plugin) { this.plugin = plugin; }

    public boolean enable() {
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        economy = rsp == null ? null : rsp.getProvider();
        return economy != null;
    }
    public boolean isAvailable() { return economy != null; }
    public Economy getEconomy() { return economy; }
}
