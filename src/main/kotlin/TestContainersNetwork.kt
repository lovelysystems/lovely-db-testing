package com.lovelysystems.db.testing

import org.testcontainers.DockerClientFactory
import org.testcontainers.containers.Network
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Returns the Docker network named [networkName]
 *
 * If no network is found a new one gets created.
 *
 * This function can be used to configure reusable docker networks along with TestContainers
 */
fun createOrUseNetwork(networkName: String): Network {
    val network: Network = Network.newNetwork()
    return try {
        val nameField = Network.NetworkImpl::class.java.getDeclaredField("name")
        nameField.setAccessible(true)
        nameField.set(network, networkName)
        val networks = DockerClientFactory.instance().client().listNetworksCmd().withNameFilter(networkName).exec()
        if (networks.isNotEmpty()) {
            val idField = Network.NetworkImpl::class.java.getDeclaredField("id")
            idField.setAccessible(true)
            idField.set(network, networks[0].id)
            val initializedField = Network.NetworkImpl::class.java.getDeclaredField("initialized")
            initializedField.setAccessible(true)
            (initializedField.get(network) as AtomicBoolean).set(true)
        }
        network
    } catch (e: NoSuchFieldException) {
        throw RuntimeException(e)
    } catch (e: IllegalAccessException) {
        throw RuntimeException(e)
    }
}
