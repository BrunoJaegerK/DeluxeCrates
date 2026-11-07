package com.brunojaegerk.deluxecrates.keys;

import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;

public final class KeyFactory {

    private static final String KEY_TAG = "DeluxeCratesKey";

    private KeyFactory() {
    }

    public static Item createKey(String crateId, String displayName, int amount) {
        Item key = Item.get(339, 0, amount);

        key.setCustomName("§dLlave §5" + displayName);

        CompoundTag keyData = new CompoundTag()
                .putString("type", "crate_key")
                .putString("crate-id", crateId)
                .putString("plugin", "DeluxeCrates")
                .putString("version", "1");

        CompoundTag rootTag = new CompoundTag()
                .putCompound(KEY_TAG, keyData);

        key.setNamedTag(rootTag);

        return key;
    }

    public static boolean isCrateKey(Item item, String crateId) {
        if (item == null || item.getId() == Item.AIR || !item.hasCompoundTag()) {
            return false;
        }

        CompoundTag rootTag = item.getNamedTag();

        if (rootTag == null || !rootTag.contains(KEY_TAG)) {
            return false;
        }

        CompoundTag keyData = rootTag.getCompound(KEY_TAG);

        return "crate_key".equals(keyData.getString("type"))
                && "DeluxeCrates".equals(keyData.getString("plugin"))
                && crateId.equals(keyData.getString("crate-id"));
    }
}
