package it.maurof00.xeonkitpvp.hook;

import it.maurof00.xeonkitpvp.KitPvPCore;

public final class HookManager {
    private final KitPvPCore plugin;
    private VaultHook vault;
    private PlaceholderHook placeholder;
    private TabHook tab;

    public HookManager(KitPvPCore plugin) { this.plugin = plugin; }

    public void enable() {
        if (plugin.getPluginConfig().vaultEnabled() && plugin.getServer().getPluginManager().getPlugin("Vault") != null) {
            vault = new VaultHook(plugin); vault.enable();
        }
        if (plugin.getPluginConfig().placeholderEnabled() && plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            placeholder = new PlaceholderHook(plugin); placeholder.register();
        }
        if (plugin.getPluginConfig().tabHookEnabled()) {
            tab = new TabHook(plugin); tab.enable();
        }
    }

    public VaultHook getVault() { return vault; }
    public PlaceholderHook getPlaceholder() { return placeholder; }
    public TabHook getTab() { return tab; }
}
