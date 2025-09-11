package com.miskaa.antiVPN;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AntiVPNCommand implements CommandExecutor, TabCompleter {
    private final AntiVPN plugin;

    public AntiVPNCommand(AntiVPN plugin) {
        this.plugin = plugin;
    }

    private boolean hasPermission(CommandSender sender) {
        if (!(sender instanceof Player)) {
            return true;
        }
        return sender.hasPermission("antivpn.admin") || sender.isOp();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!hasPermission(sender)) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Use /antivpn help for commands.");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help":
                sender.sendMessage(ChatColor.YELLOW + "AntiVPN Commands:");
                sender.sendMessage(ChatColor.GOLD + "/antivpn toggle" + ChatColor.WHITE + " - Enable/disable plugin");
                sender.sendMessage(ChatColor.GOLD + "/antivpn togglecountryfilter" + ChatColor.WHITE + " - Toggle country filter");
                sender.sendMessage(ChatColor.GOLD + "/antivpn whitelistcountry <code>" + ChatColor.WHITE + " - Whitelist country");
                sender.sendMessage(ChatColor.GOLD + "/antivpn blacklistcountry <code>" + ChatColor.WHITE + " - Remove country from whitelist");
                sender.sendMessage(ChatColor.GOLD + "/antivpn vpnmessage <msg>" + ChatColor.WHITE + " - Set VPN kick message");
                sender.sendMessage(ChatColor.GOLD + "/antivpn countrymessage <msg>" + ChatColor.WHITE + " - Set country kick message");
                sender.sendMessage(ChatColor.GOLD + "/antivpn ipbanmessage <msg>" + ChatColor.WHITE + " - Set IP-ban message");
                return true;

            case "toggle":
                plugin.setPluginEnabled(!plugin.isPluginEnabled());
                sender.sendMessage(ChatColor.GREEN + "AntiVPN is now " + (plugin.isPluginEnabled() ? "enabled" : "disabled"));
                return true;

            case "togglecountryfilter":
                plugin.setCountryCheckEnabled(!plugin.isCountryCheckEnabled());
                sender.sendMessage(ChatColor.GREEN + "Country filter is now " + (plugin.isCountryCheckEnabled() ? "enabled" : "disabled"));
                return true;

            case "whitelistcountry":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /antivpn whitelistcountry <code>");
                    return true;
                }
                List<String> whitelist = plugin.getConfig().getStringList("whitelistedCountries");
                whitelist.add(args[1].toUpperCase());
                plugin.getConfig().set("whitelistedCountries", whitelist);
                plugin.saveAndReload();
                sender.sendMessage(ChatColor.GREEN + "Whitelisted country: " + args[1].toUpperCase());
                return true;

            case "blacklistcountry":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /antivpn blacklistcountry <code>");
                    return true;
                }
                List<String> whitelist2 = plugin.getConfig().getStringList("whitelistedCountries");
                whitelist2.remove(args[1].toUpperCase());
                plugin.getConfig().set("whitelistedCountries", whitelist2);
                plugin.saveAndReload();
                sender.sendMessage(ChatColor.GREEN + "Removed country from whitelist: " + args[1].toUpperCase());
                return true;

            case "vpnmessage":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /antivpn vpnmessage <message>");
                    return true;
                }
                plugin.setVpnMessage(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                sender.sendMessage(ChatColor.GREEN + "VPN message updated!");
                return true;

            case "countrymessage":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /antivpn countrymessage <message>");
                    return true;
                }
                plugin.setCountryMessage(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                sender.sendMessage(ChatColor.GREEN + "Country message updated!");
                return true;

            case "ipbanmessage":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /antivpn ipbanmessage <message>");
                    return true;
                }
                plugin.setIpBanMessage(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                sender.sendMessage(ChatColor.GREEN + "IP-ban message updated!");
                return true;

            default:
                sender.sendMessage(ChatColor.RED + "Unknown subcommand. Use /antivpn help.");
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!hasPermission(sender)) {
            return new ArrayList<>();
        }
        if (args.length == 1) {
            return Arrays.asList("help", "toggle", "togglecountryfilter", "whitelistcountry",
                    "blacklistcountry", "vpnmessage", "countrymessage", "ipbanmessage");
        }
        return new ArrayList<>();
    }
}
