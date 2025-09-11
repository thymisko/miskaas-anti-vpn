package com.miskaa.antivpn;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Plugin(
        id = "antivpn",
        name = "AntiVPN",
        version = "0.0.4-BETA",
        authors = {"miskaa"}
)
public class AntiVPNVelocity {
    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    private boolean enabled = true;
    private boolean countryCheckEnabled = false;
    private String vpnMessage = "&cYou're using a VPN or a Proxy which isn't allowed!";
    private String countryMessage = "&cYour country is not allowed on this server!";
    private String ipBanMessage = "&cYou have been banned for repeated failed join attempts!";

    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, Integer> failedAttempts = new ConcurrentHashMap<>();

    @Inject
    public AntiVPNVelocity(ProxyServer server, Logger logger, Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        CommandManager commandManager = server.getCommandManager();
        commandManager.register("antivpn", new AntiVPNCommand(this), "avpn");
        logger.info("AntiVPNVelocity initialized!");
    }

    @Subscribe
    public void onPostLogin(PostLoginEvent event) {
        if (!enabled) return;

        Player player = event.getPlayer();
        String ip = player.getRemoteAddress().getAddress().getHostAddress();

        server.getScheduler().buildTask(this, () -> checkIp(player, ip)).schedule();
    }

    private void checkIp(Player player, String ip) {
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
                    player.disconnect(Component.text(colorize(vpnMessage)));
                    registerFailedAttempt(ip, player);
                    return;
                }

                if (countryCheckEnabled) {
                    List<String> whitelist = Collections.emptyList(); // TODO: load from config
                    if (!whitelist.contains(countryCode)) {
                        player.disconnect(Component.text(colorize(countryMessage)));
                        registerFailedAttempt(ip, player);
                    }
                }
            }
        } catch (IOException e) {
            logger.warn("Failed to check IP {}: {}", ip, e.getMessage());
        }
    }

    private void registerFailedAttempt(String ip, Player player) {
        int attempts = failedAttempts.getOrDefault(ip, 0) + 1;
        failedAttempts.put(ip, attempts);

        if (attempts >= 3) {
            player.disconnect(Component.text(colorize(ipBanMessage)));
            logger.warn("IP {} has been banned for repeated failed join attempts.", ip);
            failedAttempts.remove(ip);
        }
    }

    private String colorize(String message) {
        return message.replace("&", "§");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isCountryCheckEnabled() {
        return countryCheckEnabled;
    }

    public void setCountryCheckEnabled(boolean enabled) {
        this.countryCheckEnabled = enabled;
    }

    public void setVpnMessage(String vpnMessage) {
        this.vpnMessage = vpnMessage;
    }

    public void setCountryMessage(String countryMessage) {
        this.countryMessage = countryMessage;
    }

    public void setIpBanMessage(String ipBanMessage) {
        this.ipBanMessage = ipBanMessage;
    }
}
