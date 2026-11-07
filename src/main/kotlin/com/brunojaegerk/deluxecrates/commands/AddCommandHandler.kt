package com.brunojaegerk.deluxecrates.commands

import cn.nukkit.command.CommandSender
import com.brunojaegerk.deluxecrates.crates.CrateManager

class AddCommandHandler(
    private val crateManager: CrateManager
) {

    fun execute(sender: CommandSender, args: Array<String>) {
        if (args.size < 3) {
            sender.sendMessage(
                "§cUso: /crates addcommand <id> <comando>"
            )
            return
        }

        val crateId = args[1]
        val crate = crateManager.get(crateId)

        if (crate == null) {
            sender.sendMessage(
                "§cLa caja §f$crateId §cno existe."
            )
            return
        }

        val command = args.drop(2)
            .joinToString(" ")
            .trim()
            .removePrefix("/")
            .trim()

        if (command.isBlank()) {
            sender.sendMessage(
                "§cDebes especificar un comando válido."
            )
            return
        }

        val added = crateManager.addCommand(
            crate.id,
            command
        )

        if (!added) {
            sender.sendMessage(
                "§cNo se pudo agregar el comando a la caja."
            )
            return
        }

        sender.sendMessage("§d========== DeluxeCrates ==========")
        sender.sendMessage(
            "§aComando agregado correctamente."
        )
        sender.sendMessage(
            "§7Caja: §f${crate.displayName}"
        )
        sender.sendMessage(
            "§7Comando: §f$command"
        )
        sender.sendMessage(
            "§7Comandos configurados: §f${crate.commands.size + 1}"
        )
        sender.sendMessage("§d=================================")
    }
}