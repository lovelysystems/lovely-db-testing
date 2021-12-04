package com.lovelysystems.db.testing

import org.testcontainers.containers.Network

class PGTestSetup(
    val client: PGClientContainer,
    val server: PGServerContainer,
    private val resetScripts: List<String>
) {

    fun reset() {
        if (resetScripts.isNotEmpty()) {
            client.runFiles(*resetScripts.toTypedArray(), dbName = "postgres")
        }
    }

    fun start() {
        val network = Network.newNetwork()
        client.withNetwork(network)
        server.withNetwork(network)
        server.start()
        client.start()
    }

    fun stop() {
        client.stop()
        server.stop()
    }
}