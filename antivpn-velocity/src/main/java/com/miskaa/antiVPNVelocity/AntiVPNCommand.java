package com.miskaa.antivpn;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.List;

public class AntiVPNCommand implements SimpleCommand {
    private final AntiVPNVelocity plugin;

    public AntiVPNCommand(AntiVPNVelocity plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = invocation.arguments();

        if (args.length == 0) {
            sender.sendMessage(Component.text("§cUse /antivpn help for commands."));
            return;
        }

        switch (args[0].toLowerCase()) {
            case "help" -> {
                sender.sendMessage(Component.text("§eAntiVPN Commands:"));
                sender.sendMessage(Component.text("§6/antivpn toggle §f- Enable/disable plugin"));
                sender.sendMessage(Component.text("§6/antivpn togglecountryfilter §f- Toggle country filter"));
                sender.sendMessage(Component.text("§6/antivpn vpnmessage <msg> §f- Set VPN kick message"));
                sender.sendMessage(Component.text("§6/antivpn countrymessage <msg> §f- Set country kick message"));
                sender.sendMessage(Component.text("§6/antivpn ipbanmessage <msg> §f- Set IP-ban message"));
            }
            case "toggle" -> {
                plugin.setEnabled(!plugin.isEnabled());
                sender.sendMessage(Component.text("§aAntiVPN is now " + (plugin.isEnabled() ? "enabled" : "disabled")));
            }
            case "togglecountryfilter" -> {
                plugin.setCountryCheckEnabled(!plugin.isCountryCheckEnabled());
                sender.sendMessage(Component.text("§aCountry filter is now " + (plugin.isCountryCheckEnabled() ? "enabled" : "disabled")));
            }
            case "vpnmessage" -> {
                if (args.length < 2) {
                    sender.sendMessage(Component.text("§cUsage: /antivpn vpnmessage <message>"));
                    return;
                }
                plugin.setVpnMessage(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                sender.sendMessage(Component.text("§aVPN message updated!"));
            }
            case "countrymessage" -> {
                if (args.length < 2) {
                    sender.sendMessage(Component.text("§cUsage: /antivpn countrymessage <message>"));
                    return;
                }
                plugin.setCountryMessage(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                sender.sendMessage(Component.text("§aCountry message updated!"));
            }
            case "ipbanmessage" -> {
                if (args.length < 2) {
                    sender.sendMessage(Component.text("§cUsage: /antivpn ipbanmessage <message>"));
                    return;
                }
                plugin.setIpBanMessage(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                sender.sendMessage(Component.text("§aIP-ban message updated!"));
            }
            default -> sender.sendMessage(Component.text("§cUnknown subcommand. Use /antivpn help."));
        }
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        if (invocation.arguments().length == 1) {
            return Arrays.asList("help", "toggle", "togglecountryfilter", "vpnmessage", "countrymessage", "ipbanmessage");
        }
        return List.of();
    }
}
