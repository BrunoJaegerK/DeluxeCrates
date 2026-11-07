package com.brunojaegerk.deluxecrates.commands

import cn.nukkit.command.CommandSender
import com.brunojaegerk.deluxecrates.crates.CrateManager

class DeleteCrateHandler(
    private val crateManager: CrateManager
) {

    fun execute(sender: CommandSender, args: Array<String>) {
        if (args.size < 2) {
            sender.sendMessage("§cUso: /crates delete <nombre>")
            return
        }

        val crateId = args.drop(1).joinToString(" ")
        val crate = crateManager.get(crateId)

        if (crate == null) {
            sender.sendMessage("§cLa caja §f$crateId §cno existe.")
            return
        }

        val deleted = crateManager.delete(crate.id)

        if (!deleted) {
            sender.sendMessage("§cNo se pudo eliminar la caja.")
            return
        }

        sender.sendMessage(
            "§aLa caja §f${crate.displayName} §aha sido eliminada correctamente."
        )
    }
}