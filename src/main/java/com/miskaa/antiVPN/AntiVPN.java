package com.miskaa.antiVPN;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class AntiVPN extends JavaPlugin {

    private static AntiVPN instance;

    private boolean enabledFlag = true;
    private List<String> whitelistedCountries;
    private String vpnMessage;
    private String countryMessage;

    @Override
    public void onEnable() {
        instance = this;

        whitelistedCountries = new ArrayList<>();
        // [------ DEFAULTS ------]
        whitelistedCountries.add("PL");
        whitelistedCountries.add("CH");

        vpnMessage = "&cYou're using a VPN or a Proxy which isn't allowed!";
        countryMessage = "&cYour country is not allowed on this server!";
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        if (getCommand("antivpn") != null) {
            getCommand("antivpn").setExecutor(new AntiVPNCommand(this));
        }

        getLogger().info("AntiVPN enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("AntiVPN disabled!");
    }

    public static AntiVPN getInstance() {
        return instance;
    }

    public boolean isEnabledFlag() { return enabledFlag; }
    public void setEnabledFlag(boolean enabledFlag) { this.enabledFlag = enabledFlag; }

    public List<String> getWhitelistedCountries() { return whitelistedCountries; }

    public String getVpnMessage() { return vpnMessage; }
    public void setVpnMessage(String vpnMessage) { this.vpnMessage = vpnMessage; }

    public String getCountryMessage() { return countryMessage; }
    public void setCountryMessage(String countryMessage) { this.countryMessage = countryMessage; }
}
