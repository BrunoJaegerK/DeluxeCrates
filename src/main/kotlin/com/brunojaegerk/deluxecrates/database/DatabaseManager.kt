package com.brunojaegerk.deluxecrates.database

import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DatabaseManager(
    private val databaseFile: File
) {

    private var connection: Connection? = null

    @Synchronized
    fun initialize() {
        if (connection?.isClosed == false) {
            return
        }

        val parent = databaseFile.absoluteFile.parentFile

        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw IllegalStateException(
                "No se pudo crear el directorio de la base de datos."
            )
        }

        Class.forName("org.sqlite.JDBC")

        val jdbcUrl = "jdbc:sqlite:${databaseFile.absolutePath}"
        val newConnection = DriverManager.getConnection(jdbcUrl)

        try {
            newConnection.createStatement().use { statement ->
                statement.execute("PRAGMA foreign_keys = ON")
                statement.execute("PRAGMA busy_timeout = 5000")
            }

            createTables(newConnection)

            connection = newConnection
        } catch (exception: Exception) {
            newConnection.close()
            throw exception
        }
    }

    private fun createTables(connection: Connection) {
        connection.createStatement().use { statement ->

            // Player statistics
            statement.executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS player_stats (
                    player_uuid TEXT PRIMARY KEY,
                    player_name TEXT NOT NULL,
                    total_openings INTEGER NOT NULL DEFAULT 0,
                    last_opened_at INTEGER
                )
                """.trimIndent()
            )

            // Crate opening history
            statement.executeUpdate(
                """
                CREATE TABLE IF NOT EXISTS crate_openings (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    player_uuid TEXT NOT NULL,
                    player_name TEXT NOT NULL,
                    crate_id TEXT NOT NULL,
                    reward_id TEXT NOT NULL,
                    reward_name TEXT NOT NULL,
                    opened_at INTEGER NOT NULL,
                    FOREIGN KEY (player_uuid)
                        REFERENCES player_stats(player_uuid)
                        ON DELETE CASCADE
                )
                """.trimIndent()
            )

            // Indexes for faster queries
            statement.executeUpdate(
                """
                CREATE INDEX IF NOT EXISTS idx_crate_openings_player
                ON crate_openings(player_uuid)
                """.trimIndent()
            )

            statement.executeUpdate(
                """
                CREATE INDEX IF NOT EXISTS idx_crate_openings_crate
                ON crate_openings(crate_id)
                """.trimIndent()
            )

            statement.executeUpdate(
                """
                CREATE INDEX IF NOT EXISTS idx_crate_openings_date
                ON crate_openings(opened_at)
                """.trimIndent()
            )
        }
    }

    @Synchronized
    fun getConnection(): Connection {
        val currentConnection = connection
            ?: throw IllegalStateException(
                "La base de datos todavía no ha sido inicializada."
            )

        if (currentConnection.isClosed) {
            throw IllegalStateException(
                "La conexión con la base de datos está cerrada."
            )
        }

        return currentConnection
    }

    @Synchronized
    fun close() {
        connection?.let { currentConnection ->
            if (!currentConnection.isClosed) {
                currentConnection.close()
            }
        }

        connection = null
    }

    fun isConnected(): Boolean {
        return connection?.isClosed == false
    }

    fun getDatabaseFile(): File {
        return databaseFile
    }
}
