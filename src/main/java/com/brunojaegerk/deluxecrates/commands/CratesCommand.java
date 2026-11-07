package com.brunojaegerk.deluxecrates.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import com.brunojaegerk.deluxecrates.Main;
import com.brunojaegerk.deluxecrates.crates.CrateManager;
import com.brunojaegerk.deluxecrates.commands.SetItemHandler;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import com.brunojaegerk.deluxecrates.crates.Crate;
import com.brunojaegerk.deluxecrates.keys.KeyFactory;

public final class CratesCommand extends Command {

    private final Main plugin;

    public CratesCommand(Main plugin) {
        super(
                "crates",
                "Administración de cajas de DeluxeCrates",
                "/crates <create>"
        );

        this.plugin = plugin;
        setPermission("deluxecrates.admin");
        setPermissionMessage("§cNo tienes permiso para utilizar este comando.");
    }

    @Override
    public boolean execute(
            CommandSender sender,
            String commandLabel,
            String[] args
    ) {
        if (!testPermission(sender)) {
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                handleCreate(sender, args);
                break;

            case "setitem":
                new SetItemHandler(plugin.getCrateManager()).execute(sender, args);
                break;

            case "givekey":
                handleGiveKey(sender, args);
                break;

            default:
                sendHelp(sender);
                break;
        }

        return true;
    }

    private void handleCreate(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUso: /crates create <nombre>");
            return;
        }

        String displayName = String.join(
                " ",
                java.util.Arrays.copyOfRange(args, 1, args.length)
        );

        CrateManager crateManager = plugin.getCrateManager();

        boolean created = crateManager.create(
                displayName,
                displayName
        );

        if (!created) {
            sender.sendMessage(
                    "§cNo se pudo crear la caja. Comprueba que el nombre sea válido y que no exista."
            );
            return;
        }

        sender.sendMessage(
                "§aLa caja §f" + displayName + " §ase creó correctamente."
        );
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§5§lDeluxeCrates §7- §fComandos");
        sender.sendMessage("§d/crates create <nombre> §7- Crear una caja");
        sender.sendMessage("§d/crates setitem <nombre> §7- Establecer el objeto de una caja");
        sender.sendMessage("§d/crates givekey <jugador> <caja> [cantidad] §7- Entregar llaves");
    }

    private void handleGiveKey(CommandSender sender, String[] args) {
        if (args.length < 3 || args.length > 4) {
            sender.sendMessage("§cUso: /crates givekey <jugador> <caja> [cantidad]");
            return;
        }

        String playerName = args[1];

        Player target = plugin.getServer().getPlayerExact(playerName);

        if (target == null || !target.isOnline()) {
            sender.sendMessage("§cEl jugador §f" + playerName + " §cno está conectado.");
            return;
        }

        String crateId = args[2];

        Crate crate = plugin.getCrateManager().get(crateId);

        if (crate == null) {
            sender.sendMessage("§cLa caja §f" + crateId + " §cno existe.");
            return;
        }

        int amount = 1;

        if (args.length == 4) {
            try {
                amount = Integer.parseInt(args[3]);
            } catch (NumberFormatException exception) {
                sender.sendMessage("§cLa cantidad debe ser un número válido.");
                return;
            }
        }

        if (amount < 1 || amount > 64) {
            sender.sendMessage("§cLa cantidad debe estar entre 1 y 64.");
            return;
        }

        Item key = KeyFactory.createKey(
                crate.getId(),
                crate.getDisplayName(),
                amount
        );

        Item[] leftovers = target.getInventory().addItem(key);

        int remaining = 0;

        for (Item leftover : leftovers) {
            remaining += leftover.getCount();
        }

        int delivered = amount - remaining;

        if (remaining > 0) {
            sender.sendMessage(
                    "§eNo se pudieron entregar §f" + remaining
                            + " §ellave(s) porque el inventario está lleno."
            );
        }

        if (delivered > 0) {
            sender.sendMessage(
                    "§aSe entregaron §f" + delivered + " §allave(s) de §f"
                            + crate.getDisplayName() + " §aal jugador §f"
                            + target.getName() + "§a."
            );

            target.sendMessage(
                    "§aHas recibido §f" + delivered + " §allave(s) de §f"
                            + crate.getDisplayName() + "§a."
            );
        }
    }
}
