package com.miskaa.antiVPN;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class AntiVPN extends JavaPlugin {
    private static AntiVPN instance;

    private boolean enabled = true;
    private boolean countryCheckEnabled = false; // default
    private String vpnMessage;
    private String countryMessage;
    private String ipBanMessage;

    private final Map<String, Integer> failedAttempts = new HashMap<>();

    public static AntiVPN getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        loadConfigValues();

        getCommand("antivpn").setExecutor(new AntiVPNCommand(this));
        getCommand("antivpn").setTabCompleter(new AntiVPNCommand(this));

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getLogger().info("AntiVPN enabled!");
    }

    @Override
    public void onDisable() {
        saveConfig();
        getLogger().info("AntiVPN disabled!");
    }

    public void loadConfigValues() {
        FileConfiguration config = getConfig();
        vpnMessage = config.getString("vpnMessage", "&cYou're using a VPN or a Proxy which isn't allowed!");
        countryMessage = config.getString("countryMessage", "&cYour country is not allowed on this server!");
        ipBanMessage = config.getString("ipBanMessage", "&cYou have been banned for repeated failed join attempts!");
    }

    public void saveAndReload() {
        saveConfig();
        reloadConfig();
        loadConfigValues();
    }

    public boolean isPluginEnabled() {
        return enabled;
    }

    public void setPluginEnabled(boolean enabled) {
        this.enabled = enabled;
        getConfig().set("enabled", enabled);
        saveAndReload();
    }

    public boolean isCountryCheckEnabled() {
        return countryCheckEnabled;
    }

    public void setCountryCheckEnabled(boolean enabled) {
        this.countryCheckEnabled = enabled;
        getConfig().set("countryCheckEnabled", enabled);
        saveAndReload();
    }

    public String getVpnMessage() {
        return vpnMessage;
    }

    public void setVpnMessage(String vpnMessage) {
        this.vpnMessage = vpnMessage;
        getConfig().set("vpnMessage", vpnMessage);
        saveAndReload();
    }

    public String getCountryMessage() {
        return countryMessage;
    }

    public void setCountryMessage(String countryMessage) {
        this.countryMessage = countryMessage;
        getConfig().set("countryMessage", countryMessage);
        saveAndReload();
    }

    public String getIpBanMessage() {
        return ipBanMessage;
    }

    public void setIpBanMessage(String ipBanMessage) {
        this.ipBanMessage = ipBanMessage;
        getConfig().set("ipBanMessage", ipBanMessage);
        saveAndReload();
    }

    public void registerFailedAttempt(String ip) {
        int attempts = failedAttempts.getOrDefault(ip, 0) + 1;
        failedAttempts.put(ip, attempts);

        if (attempts >= 3) {
            Bukkit.getBanList(org.bukkit.BanList.Type.IP).addBan(ip, ipBanMessage, null, "AntiVPN");
            Bukkit.getOnlinePlayers().stream()
                    .filter(p -> p.getAddress() != null && p.getAddress().getAddress().getHostAddress().equals(ip))
                    .forEach(p -> p.kickPlayer(ipBanMessage));
            getLogger().warning("IP " + ip + " has been banned for repeated failed join attempts.");
            failedAttempts.remove(ip);
        }
    }
}
