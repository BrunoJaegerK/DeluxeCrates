package com.brunojaegerk.deluxecrates.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;

import com.brunojaegerk.deluxecrates.Main;
import com.brunojaegerk.deluxecrates.crates.Crate;
import com.brunojaegerk.deluxecrates.crates.ItemSerializer;

import me.iwareq.fakeinventories.FakeInventory;
import cn.nukkit.plugin.Plugin;

public final class PreviewCrateHandler {

    private final Main plugin;

    public PreviewCrateHandler(Main plugin) {
        this.plugin = plugin;
    }

    public void execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(
                    "§cEste comando solo puede utilizarse dentro del juego."
            );
            return;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            player.sendMessage(
                    "§cUso: /crates preview <nombre>"
            );
            return;
        }

        Plugin fakeInventories = plugin.getServer()
                .getPluginManager()
                .getPlugin("FakeInventories");

        if (fakeInventories == null || !fakeInventories.isEnabled()) {
            player.sendMessage(
                    "§cFakeInventories no está instalado o habilitado."
            );
            return;
        }

        String crateId = String.join(
                " ",
                java.util.Arrays.copyOfRange(args, 1, args.length)
        );

        Crate crate = plugin.getCrateManager().get(crateId);

        if (crate == null) {
            player.sendMessage(
                    "§cLa caja §f" + crateId + " §cno existe."
            );
            return;
        }

        String itemData = crate.getItemData();

        if (itemData == null || itemData.isBlank()) {
            player.sendMessage(
                    "§cEsta caja todavía no tiene un objeto configurado."
            );
            return;
        }

        Item previewItem = ItemSerializer.INSTANCE.deserialize(itemData);

        if (previewItem == null || previewItem.getId() == Item.AIR) {
            player.sendMessage(
                    "§cNo se pudo recuperar el objeto de esta caja."
            );
            return;
        }

        FakeInventory inventory = new FakeInventory(
                InventoryType.CHEST,
                "§5Vista previa: " + crate.getDisplayName()
        );

        // Colocar el objeto en el centro del inventario.
        inventory.setItem(13, previewItem);

        // Bloquear cualquier interacción con los objetos.
        inventory.setDefaultItemHandler((item, event) -> {
            event.setCancelled(true);
        });

        player.addWindow(inventory);

        player.sendMessage(
                "§aMostrando la vista previa de §f"
                        + crate.getDisplayName() + "§a."
        );
    }
}

