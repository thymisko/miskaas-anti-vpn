package com.miskaa.antiVPN;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class AntiVPNCommand implements CommandExecutor {

    private final AntiVPN plugin;

    public AntiVPNCommand(AntiVPN plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "toggle" -> {
                boolean enabled = plugin.isEnabledFlag();
                plugin.setEnabledFlag(!enabled);
                sender.sendMessage(ChatColor.GREEN + "AntiVPN is now " + (!enabled ? "enabled" : "disabled"));
            }

            case "whitelistcountry" -> {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /antivpn whitelistcountry <country code>");
                    return true;
                }
                String code = args[1].toUpperCase();
                List<String> list = plugin.getWhitelistedCountries();
                if (!list.contains(code)) {
                    list.add(code);
                    sender.sendMessage(ChatColor.GREEN + "Added " + code + " to whitelist");
                } else sender.sendMessage(ChatColor.YELLOW + code + " is already whitelisted");
            }

            case "blacklistcountry" -> {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /antivpn blacklistcountry <country code>");
                    return true;
                }
                String code = args[1].toUpperCase();
                List<String> list = plugin.getWhitelistedCountries();
                if (list.contains(code)) {
                    list.remove(code);
                    sender.sendMessage(ChatColor.GREEN + "Removed " + code + " from whitelist");
                } else sender.sendMessage(ChatColor.YELLOW + code + " is not in whitelist");
            }

            case "togglecountryfilter" -> {
                boolean current = plugin.isCountryCheckEnabled();
                plugin.setCountryCheckEnabled(!current);
                sender.sendMessage(ChatColor.GREEN + "Country whitelist check is now " + (!current ? "enabled" : "disabled"));
            }

            case "countrymessage" -> {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /antivpn countrymessage <message>");
                    return true;
                }
                String msg = String.join(" ", args).substring(sub.length() + 1);
                plugin.setCountryMessage(msg);
                sender.sendMessage(ChatColor.GREEN + "Country kick message updated");
            }

            case "vpnmessage" -> {
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /antivpn vpnmessage <message>");
                    return true;
                }
                String msg = String.join(" ", args).substring(sub.length() + 1);
                plugin.setVpnMessage(msg);
                sender.sendMessage(ChatColor.GREEN + "VPN kick message updated");
            }

            default -> sender.sendMessage(ChatColor.RED + "Unknown subcommand: " + sub + ". Type /antivpn help for commands.");
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "--- AntiVPN Help ---");
        sender.sendMessage(ChatColor.YELLOW + "/antivpn help " + ChatColor.WHITE + "- Show this help message");
        sender.sendMessage(ChatColor.YELLOW + "/antivpn toggle " + ChatColor.WHITE + "- Enable or disable the plugin");
        sender.sendMessage(ChatColor.YELLOW + "/antivpn whitelistcountry <code> " + ChatColor.WHITE + "- Add a country to whitelist");
        sender.sendMessage(ChatColor.YELLOW + "/antivpn blacklistcountry <code> " + ChatColor.WHITE + "- Remove a country from whitelist");
        sender.sendMessage(ChatColor.YELLOW + "/antivpn togglecountryfilter " + ChatColor.WHITE + "- Enable or disable country whitelist check globally");
        sender.sendMessage(ChatColor.YELLOW + "/antivpn countrymessage <message> " + ChatColor.WHITE + "- Set message for country restriction");
        sender.sendMessage(ChatColor.YELLOW + "/antivpn vpnmessage <message> " + ChatColor.WHITE + "- Set message for VPN/proxy kick");
    }
}
