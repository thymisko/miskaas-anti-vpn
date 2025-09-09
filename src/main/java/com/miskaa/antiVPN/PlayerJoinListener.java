package com.miskaa.antiVPN;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PlayerJoinListener implements Listener {
    private final AntiVPN plugin;
    private final ObjectMapper mapper = new ObjectMapper();

    public PlayerJoinListener(AntiVPN plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (!plugin.isPluginEnabled()) {
            return;
        }

        String ip = event.getAddress().getHostAddress();
        try {
            URL url = new URL("http://ip-api.com/json/" + ip + "?fields=proxy,hosting,countryCode");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            try (InputStream input = connection.getInputStream()) {
                JsonNode json = mapper.readTree(input);

                boolean proxy = json.path("proxy").asBoolean(false);
                boolean hosting = json.path("hosting").asBoolean(false);
                String countryCode = json.path("countryCode").asText("");

                if (proxy || hosting) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                            ChatColor.translateAlternateColorCodes('&', plugin.getVpnMessage()));
                    plugin.registerFailedAttempt(ip);
                    return;
                }

                if (plugin.isCountryCheckEnabled()) {
                    if (!plugin.getConfig().getStringList("whitelistedCountries").contains(countryCode)) {
                        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                ChatColor.translateAlternateColorCodes('&', plugin.getCountryMessage()));
                        plugin.registerFailedAttempt(ip);
                    }
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to check IP " + ip + ": " + e.getMessage());
        }
    }
}
