package com.brunojaegerk.deluxecrates.commands

import cn.nukkit.Player
import cn.nukkit.command.CommandSender
import cn.nukkit.item.Item
import com.brunojaegerk.deluxecrates.crates.CrateManager
import com.brunojaegerk.deluxecrates.crates.ItemSerializer

class SetItemHandler(
    private val crateManager: CrateManager
) {

    fun execute(sender: CommandSender, args: Array<String>) {
        if (sender !is Player) {
            sender.sendMessage("§cEste comando solo puede utilizarse dentro del juego.")
            return
        }

        if (args.size < 2) {
            sender.sendMessage("§cUso: /crates setitem <nombre>")
            return
        }

        val crateId = args.drop(1).joinToString(" ")
        val crate = crateManager.get(crateId)

        if (crate == null) {
            sender.sendMessage("§cLa caja §f$crateId §cno existe.")
            return
        }

        val item = sender.inventory.itemInHand

        if (item.id == Item.AIR || item.count <= 0) {
            sender.sendMessage("§cDebes tener un objeto en la mano.")
            return
        }

        val serializedItem = ItemSerializer.serialize(item)

        if (!crateManager.updateItemData(crate.id, serializedItem)) {
            sender.sendMessage("§cNo se pudo guardar el objeto de la caja.")
            return
        }

        sender.sendMessage(
            "§aSe estableció el objeto de la caja §f${crate.displayName} §acorrectamente."
        )
    }
}