package com.brunojaegerk.deluxecrates.crates

import cn.nukkit.utils.Config
import java.io.File
import java.util.Locale

class CrateManager(dataFolder: File) {

    private val cratesFile = File(dataFolder, "crates.yml")
    private val config = Config(cratesFile, Config.YAML)

    private val crates = linkedMapOf<String, Crate>()

    init {
        load()
    }

    @Synchronized
    fun create(id: String, displayName: String): Boolean {
        val normalizedId = normalizeId(id)

        if (normalizedId.isBlank() || displayName.isBlank()) {
            return false
        }

        if (crates.containsKey(normalizedId)) {
            return false
        }

        crates[normalizedId] = Crate(
            id = normalizedId,
            displayName = displayName.trim()
        )

        save()
        return true
    }

    @Synchronized
    fun get(id: String): Crate? {
        return crates[normalizeId(id)]
    }

    @Synchronized
    fun getAll(): List<Crate> {
        return crates.values.toList()
    }

    @Synchronized
    fun exists(id: String): Boolean {
        return crates.containsKey(normalizeId(id))
    }

    @Synchronized
    fun delete(id: String): Boolean {
        val normalizedId = normalizeId(id)

        if (crates.remove(normalizedId) == null) {
            return false
        }

        save()
        return true
    }

    @Synchronized
    fun save() {
        val serializedCrates = linkedMapOf<String, Any>()

        crates.forEach { (id, crate) ->
            serializedCrates[id] = linkedMapOf(
                "display-name" to crate.displayName,
                "item-data" to (crate.itemData ?: ""),
                "commands" to crate.commands
            )
        }

        config.set("crates", serializedCrates)
        config.save()
    }

    @Synchronized
    fun load() {
        crates.clear()

        config.reload()

        val allData = config.getAll()
        val crateSection = allData["crates"] as? Map<*, *> ?: return

        crateSection.forEach { (rawId, rawData) ->
            val id = rawId?.toString() ?: return@forEach
            val data = rawData as? Map<*, *> ?: return@forEach

            val displayName = data["display-name"]?.toString() ?: id
            val rawItemData = data["item-data"]?.toString()
            val itemData = rawItemData?.takeIf { it.isNotBlank() }

            val commands = (data["commands"] as? List<*>)
                ?.mapNotNull { it?.toString() }
                ?: emptyList()

            crates[id] = Crate(
                id = id,
                displayName = displayName,
                itemData = itemData,
                commands = commands
            )
        }
    }

    private fun normalizeId(id: String): String {
        return id.trim()
            .lowercase(Locale.ROOT)
            .replace(Regex("[^a-z0-9_-]"), "_")
    }

    @Synchronized
    fun updateItemData(id: String, itemData: String): Boolean {
        val normalizedId = normalizeId(id)
        val crate = crates[normalizedId] ?: return false

        crates[normalizedId] = crate.copy(
            itemData = itemData
        )

        save()
        return true
    }

    @Synchronized
    fun addCommand(id: String, command: String): Boolean {
        val normalizedId = normalizeId(id)
        val crate = crates[normalizedId] ?: return false

        val normalizedCommand = command.trim()
            .removePrefix("/")
            .trim()

        if (normalizedCommand.isBlank()) {
            return false
        }

        crates[normalizedId] = crate.copy(
            commands = crate.commands + normalizedCommand
        )

        save()
        return true
    }
}