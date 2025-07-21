package bwillows.veinminer.commands;

import bwillows.veinminer.VeinMiner;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class VeinMinerCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("itemstackplaceholderapi.*")) {
            String message = VeinMiner.instance.veinMinerConfig.langYml.getString("no-permission");
            if (!(message == null) && !message.trim().isEmpty()) {
                message = message.replace("§", "&");
                message = ChatColor.translateAlternateColorCodes('&', message);
                message = message.replace("%version%", VeinMiner.version);
                sender.sendMessage(message);
            }
            return true;
        }

        if (args.length == 0) {
            List<String> message = VeinMiner.instance.veinMinerConfig.langYml.getStringList("no-argument");
            if(!(message == null)) {
                for (String line : message) {
                    line = line.replace("§", "&");
                    line = ChatColor.translateAlternateColorCodes('&', line);
                    line = line.replace("%version%", VeinMiner.version);
                    sender.sendMessage(line);
                }
            }
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help":
            {
                List<String> message = VeinMiner.instance.veinMinerConfig.langYml.getStringList("help");
                if(!(message == null)) {
                    for (String line : message) {
                        line = line.replace("§", "&");
                        line = ChatColor.translateAlternateColorCodes('&', line);
                        line = line.replace("%version%", VeinMiner.version);
                        sender.sendMessage(line);
                    }
                }
            }
            return true;
            case "ver":
            case "version":
            {
                String message = VeinMiner.instance.veinMinerConfig.langYml.getString("version");
                if(!(message == null) && !message.trim().isEmpty()) {
                    message = message.replace("§", "&");
                    message = ChatColor.translateAlternateColorCodes('&', message);
                    message = message.replace("%version%", VeinMiner.version);
                    sender.sendMessage(message);
                }
            }
            return true;
            case "reload":
            {
                VeinMiner.instance.veinMinerConfig.reload();
                String message = VeinMiner.instance.veinMinerConfig.langYml.getString("reload");
                if (!(message == null) && !message.trim().isEmpty()) {
                    message = message.replace("§", "&");
                    message = ChatColor.translateAlternateColorCodes('&', message);
                    message = message.replace("%version%", VeinMiner.version);
                    sender.sendMessage(message);
                }
            }
            return true;
            default:
            {
                String message = VeinMiner.instance.veinMinerConfig.langYml.getString("invalid-argument");
                if (!(message == null) && !message.trim().isEmpty()) {
                    message = message.replace("§", "&");
                    message = ChatColor.translateAlternateColorCodes('&', message);
                    message = message.replace("%version%", VeinMiner.version);
                    sender.sendMessage(message);
                }
            }
            return  true;
        }
    }
}
