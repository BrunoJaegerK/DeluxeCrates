package com.brunojaegerk.deluxecrates;

import cn.nukkit.plugin.PluginBase;
import com.brunojaegerk.deluxecrates.commands.CratesCommand;
import com.brunojaegerk.deluxecrates.crates.CrateManager;

public final class Main extends PluginBase {

    private static Main instance;

    private CrateManager crateManager;

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

        // Inicializar el administrador de cajas
        crateManager = new CrateManager(getDataFolder());

        getLogger().info(
                "§aSe cargaron " + crateManager.getAll().size() + " cajas."
        );

        // Registrar comandos
        getServer().getCommandMap().register(
                "deluxecrates",
                new CratesCommand(this)
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
        if (crateManager != null) {
            crateManager.save();
        }

        getLogger().info("§cDeluxeCrates deshabilitado.");

        instance = null;
    }

    private void saveDefaultResources() {
        saveResource("config.yml", false);
        saveResource("crates.yml", false);
        saveResource("messages.yml", false);
    }

    public CrateManager getCrateManager() {
        return crateManager;
    }

    public static Main getInstance() {
        return instance;
    }
}


