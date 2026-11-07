package com.brunojaegerk.deluxecrates;

import cn.nukkit.plugin.PluginBase;

public final class Main extends PluginBase {

    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultResources();

        getLogger().info("§d================================");
        getLogger().info("§5       DeluxeCrates");
        getLogger().info("§7   Advanced Crate System");
        getLogger().info("§d================================");

        // Comprobar la integración con Kotlin
        getLogger().info(
                "§a" + KotlinBootstrap.startupMessage()
        );

        getLogger().info("§7Economía: desactivada");
        getLogger().info("§7Llaves: físicas");
        getLogger().info("§7Almacenamiento: SQLite");

        getLogger().info("§aDeluxeCrates habilitado correctamente.");
        getLogger().info(
                "§7Versión: " + getDescription().getVersion()
        );
    }

    @Override
    public void onDisable() {
        getLogger().info("§cDeluxeCrates deshabilitado.");

        instance = null;
    }

    private void saveDefaultResources() {
        saveResource("config.yml", false);
        saveResource("crates.yml", false);
        saveResource("messages.yml", false);
    }

    public static Main getInstance() {
        return instance;
    }
}

