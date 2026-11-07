package com.brunojaegerk.deluxecrates.commands;

import cn.nukkit.command.CommandSender;

import com.brunojaegerk.deluxecrates.Main;
import com.brunojaegerk.deluxecrates.crates.Crate;

import java.util.List;

public final class ListCommandsHandler {

    private final Main plugin;

    public ListCommandsHandler(Main plugin) {
        this.plugin = plugin;
    }

    public void execute(CommandSender sender, String[] args) {

        if (args.length != 2) {
            sender.sendMessage(
                    "§cUso: /crates listcommands <id>"
            );
            return;
        }

        String crateId = args[1];

        Crate crate = plugin.getCrateManager().get(crateId);

        if (crate == null) {
            sender.sendMessage(
                    "§cLa caja §f" + crateId + " §cno existe."
            );
            return;
        }

        List<String> commands = crate.getCommands();

        sender.sendMessage("§5§l========== DeluxeCrates ==========");
        sender.sendMessage(
                "§7Caja: §f" + crate.getDisplayName()
        );
        sender.sendMessage(
                "§7ID: §f" + crate.getId()
        );
        sender.sendMessage(
                "§7Comandos configurados: §f" + commands.size()
        );
        sender.sendMessage("");

        if (commands.isEmpty()) {
            sender.sendMessage(
                    "§eEsta caja no tiene comandos configurados."
            );
        } else {
            for (int i = 0; i < commands.size(); i++) {
                sender.sendMessage(
                        "§d" + (i + 1) + ". §f" + commands.get(i)
                );
            }
        }

        sender.sendMessage("");
        sender.sendMessage("§5§l=================================");
    }
}
