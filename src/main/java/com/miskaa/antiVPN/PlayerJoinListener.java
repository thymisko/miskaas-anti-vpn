package com.miskaa.antiVPN;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

public class PlayerJoinListener implements Listener {

    private final AntiVPN plugin;

    public PlayerJoinListener(AntiVPN plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        if (!plugin.isEnabledFlag()) return;

        InetAddress ip = event.getAddress();
        String ipStr = ip.getHostAddress();

        try {
            URL url = new URL("http://ip-api.com/json/" + ipStr + "?fields=country,countryCode,proxy,hosting");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            String json = response.toString();
            boolean proxy = json.contains("\"proxy\":true") || json.contains("\"hosting\":true");
            String countryCode = extractCountryCode(json);

            if (proxy) {
                event.disallow(
                        AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                        ChatColor.translateAlternateColorCodes('&', plugin.getVpnMessage())
                );
                return;
            }

            if (!plugin.getWhitelistedCountries().contains(countryCode)) {
                event.disallow(
                        AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                        ChatColor.translateAlternateColorCodes('&', plugin.getCountryMessage())
                );
            }

        } catch (Exception e) {
            plugin.getLogger().warning("Failed to check IP: " + e.getMessage());
        }
    }

    private String extractCountryCode(String json) {
        String key = "\"countryCode\":\"";
        int start = json.indexOf(key);
        if (start == -1) return "??";
        start += key.length();
        int end = json.indexOf("\"", start);
        if (end == -1) return "??";
        return json.substring(start, end).toUpperCase();
    }
}
