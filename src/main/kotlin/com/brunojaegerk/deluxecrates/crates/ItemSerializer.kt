package com.brunojaegerk.deluxecrates.crates

import cn.nukkit.item.Item
import java.util.Base64

object ItemSerializer {

    fun serialize(item: Item): String {
        val encodedTags = Base64.getEncoder()
            .encodeToString(item.compoundTag)

        return listOf(
            item.id.toString(),
            item.damage.toString(),
            item.count.toString(),
            encodedTags
        ).joinToString(";")
    }

    fun deserialize(data: String): Item? {
        return try {
            val parts = data.split(";", limit = 4)

            if (parts.size != 4) {
                return null
            }

            val id = parts[0].toInt()
            val damage = parts[1].toInt()
            val count = parts[2].toInt()
            val tags = Base64.getDecoder().decode(parts[3])

            Item.get(id, damage, count, tags)
        } catch (_: Exception) {
            null
        }
    }
}