package com.brunojaegerk.deluxecrates.commands

import cn.nukkit.command.CommandSender
import cn.nukkit.item.Item
import com.brunojaegerk.deluxecrates.Main
import com.brunojaegerk.deluxecrates.keys.KeyFactory

class KeyAllHandler(
    private val plugin: Main
) {

    fun execute(sender: CommandSender, args: Array<String>) {
        if (args.size < 2 || args.size > 3) {
            sender.sendMessage("§cUso: /crates keyall <caja> [cantidad]")
            return
        }

        val crateId = args[1]
        val crate = plugin.crateManager.get(crateId)

        if (crate == null) {
            sender.sendMessage("§cLa caja §f$crateId §cno existe.")
            return
        }

        var amount = 1

        if (args.size == 3) {
            amount = args[2].toIntOrNull() ?: run {
                sender.sendMessage("§cLa cantidad debe ser un número válido.")
                return
            }
        }

        if (amount !in 1..64) {
            sender.sendMessage("§cLa cantidad debe estar entre 1 y 64.")
            return
        }

        val players = plugin.server.getOnlinePlayersList()

        if (players.isEmpty()) {
            sender.sendMessage("§cNo hay jugadores conectados.")
            return
        }

        var deliveredPlayers = 0
        var deliveredKeys = 0
        var playersWithFullInventory = 0
        var remainingKeys = 0

        for (target in players) {
            val key = KeyFactory.createKey(
                crate.id,
                crate.displayName,
                amount
            )

            val leftovers: Array<Item> = target.inventory.addItem(key)

            var remaining = 0

            for (leftover in leftovers) {
                remaining += leftover.count
            }

            val delivered = amount - remaining

            if (delivered > 0) {
                deliveredPlayers++
                deliveredKeys += delivered

                target.sendMessage(
                    "§aHas recibido §f$delivered §allave(s) de §f${crate.displayName}§a."
                )
            }

            if (remaining > 0) {
                playersWithFullInventory++
                remainingKeys += remaining

                target.sendMessage(
                    "§cNo se pudieron guardar §f$remaining §cllave(s) porque tu inventario está lleno."
                )
            }
        }

        sender.sendMessage("§d========== DeluxeCrates ==========")
        sender.sendMessage("§aEntrega global finalizada.")
        sender.sendMessage("§7Caja: §f${crate.displayName}")
        sender.sendMessage("§7Jugadores conectados: §f${players.size}")
        sender.sendMessage("§7Jugadores que recibieron llaves: §f$deliveredPlayers")
        sender.sendMessage("§7Llaves entregadas: §f$deliveredKeys")

        if (playersWithFullInventory > 0) {
            sender.sendMessage(
                "§eJugadores con inventario lleno: §f$playersWithFullInventory"
            )
            sender.sendMessage(
                "§eLlaves no entregadas: §f$remainingKeys"
            )
        }

        sender.sendMessage("§d================================")
    }
}
