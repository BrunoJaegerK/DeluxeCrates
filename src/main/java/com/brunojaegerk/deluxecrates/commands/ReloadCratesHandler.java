package com.brunojaegerk.deluxecrates.commands;

import cn.nukkit.command.CommandSender;
import com.brunojaegerk.deluxecrates.crates.CrateManager;

public final class ReloadCratesHandler {

    private final CrateManager crateManager;

    public ReloadCratesHandler(CrateManager crateManager) {
        this.crateManager = crateManager;
    }

    public void execute(CommandSender sender) {
        try {
            crateManager.load();

            int total = crateManager.getAll().size();

            sender.sendMessage("§d========== DeluxeCrates ==========");
            sender.sendMessage("§aLas cajas se recargaron correctamente.");
            sender.sendMessage("§7Cajas cargadas: §f" + total);
            sender.sendMessage("§d=================================");
        } catch (Exception exception) {
            sender.sendMessage("§cOcurrió un error al recargar las cajas.");
            exception.printStackTrace();
        }
    }
}