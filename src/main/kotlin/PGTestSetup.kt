package com.lovelysystems.db.testing

import org.testcontainers.containers.Network

class PGTestSetup(
    val client: PGClientContainer,
    val server: PGServerContainer,
    private val resetScripts: List<String>,
    private val reuseIdent: String? = null,
    private val fixedPort: Int? = null,
) {

    fun reset() {
        if (resetScripts.isNotEmpty()) {
            client.runFiles(*resetScripts.toTypedArray(), dbName = "postgres")
        }
    }

    fun start() {
        val network = if (reuseIdent != null) {
            // use a network named after the site
            createOrUseNetwork("ldbt_$reuseIdent")
        } else {
            Network.newNetwork()
        }
        if (reuseIdent == null) {
            client.withNetworkAliases("client")
            server.withNetworkAliases("server")
        }

        client.withNetwork(network)
        server.withNetwork(network)
        server.withReuse(reuseIdent != null)
        fixedPort?.let {
            server.withFixedPort(it)
        }


        server.start()
        client.start()
    }

    fun stop() {
        if (reuseIdent == null) {
            client.stop()
            server.stop()
        }
    }
}
