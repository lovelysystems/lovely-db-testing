package com.lovelysystems.db.testing

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.testcontainers.containers.Network
import kotlin.io.path.Path

data class PGTestSettings(
    val clientImage: String = "lovelysystems/docker-postgres:0.1.0",
    val serverImage: String = "lovelysystems/docker-postgres:0.1.0",
    val devDir: String = "localdev/volumes/pgdev",
    val testDir: String = "src/test/sql",
    val defaultDB: String = "postgres",
    val testFilePattern: String = "**.sql",
    val resetScripts: List<String> = emptyList(),
    val clientConfiguration: PGClientContainer.() -> Unit = {}

) {
    fun create(): PGTestSetup {
        return PGTestSetup(
            PGClientContainer(
                Path(testDir),
                clientImage,
                devDir = Path(devDir),
                defaultDB = defaultDB,
                testFilePattern = testFilePattern,
                clientConfiguration = clientConfiguration
            ),
            PGServerContainer(serverImage),
            resetScripts = resetScripts
        )
    }
}

class PGTestSetup(val client: PGClientContainer, val server: PGServerContainer, val resetScripts: List<String>) :
    BeforeAllCallback, AfterAllCallback, BeforeEachCallback {

    override fun beforeEach(context: ExtensionContext?) {
        if (resetScripts.isNotEmpty()) {
            client.runFiles(*resetScripts.toTypedArray(), dbName = "postgres")
        }
    }

    override fun beforeAll(context: ExtensionContext) {
        val network = Network.newNetwork()
        client.withNetwork(network)
        server.withNetwork(network)
        server.start()
        client.start()
    }

    override fun afterAll(context: ExtensionContext) {
        client.stop()
        server.stop()
    }
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class DBTest(settings: PGTestSettings = PGTestSettings()) {

    @JvmField
    @RegisterExtension
    val pg: PGTestSetup = settings.create()

    fun sqlTestPaths(): List<String> {
        val paths = pg.client.testPaths()
        assert(paths.isNotEmpty()) { "no sql files found in ${pg.client.testDir}" }
        return paths.map { it.toString() }
    }

    @ParameterizedTest
    @MethodSource("sqlTestPaths")
    fun testSQL(sqlFile: String) {
        pg.client.runTestFile(sqlFile)
    }
}

