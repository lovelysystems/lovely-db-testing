package com.lovelysystems.db.testing

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import kotlin.io.path.Path

/**
 * Settings for a PGTestSetup
 *
 * @param clientImage docker image for the client container
 * @param serverImage docker image for the server container
 * @param devDir directory with sql files to be mapped into the server container
 * @param testDir directory with sql files to be tested
 * @param defaultDB default database to connect to
 * @param testFilePattern glob pattern for sql files to be tested
 * @param resetScripts list of sql files to be executed before each reset
 * @param reuseIdent enables reuse of the server container. If set to null, a new container is started for each test.
 * @param fixedPort if set, the server's postgres port will be exposed on this host port. If null, a random port is used.
 * @param serverConfiguration configuration block for the server container
 * @param clientConfiguration configuration block for the client container
 */
data class PGTestSettings(
    val clientImage: String = "lovelysystems/docker-postgres:14.1.0-client",
    val serverImage: String = "lovelysystems/docker-postgres:14.1.0",
    val devDir: String? = "localdev/volumes/pgdev",
    val testDir: String? = "src/test/sql",
    val defaultDB: String = "postgres",
    val testFilePattern: String = "**.sql",
    val resetScripts: List<String> = emptyList(),
    val reuseIdent: String? = null,
    val fixedPort: Int? = null,
    val serverConfiguration: PGServerContainer.() -> Unit = {},
    val clientConfiguration: PGClientContainer.() -> Unit = {},
) {
    fun create(): PGTestSetup {
        return PGTestSetup(
            PGClientContainer(
                testDir = testDir?.let { Path(it) },
                clientImage,
                devDir = devDir?.let { Path(it) },
                defaultDB = defaultDB,
                testFilePattern = testFilePattern,
                configureBlock = clientConfiguration
            ),
            PGServerContainer(serverImage, configureBlock = serverConfiguration),
            resetScripts = resetScripts,
            reuseIdent = reuseIdent,
            fixedPort = fixedPort
        )
    }
}

class PGTestJunitSetup(val pgSetup: PGTestSetup) : BeforeAllCallback, AfterAllCallback, BeforeEachCallback {

    val client: PGClientContainer = pgSetup.client
    val server: PGServerContainer = pgSetup.server

    override fun beforeAll(context: ExtensionContext?) {
        pgSetup.start()
    }

    override fun afterAll(context: ExtensionContext?) {
        pgSetup.stop()
    }

    override fun beforeEach(context: ExtensionContext?) {
        pgSetup.reset()
    }
}

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class DBTest(settings: PGTestSettings = PGTestSettings()) {

    @JvmField
    @RegisterExtension
    val pg: PGTestJunitSetup = PGTestJunitSetup(settings.create())

    fun sqlTestPaths(): List<String> {
        val paths = pg.client.testPaths().toList()
        assert(paths.isNotEmpty()) { "no sql files found in ${pg.client.testDir}" }
        return paths.map { it.toString() }
    }

    @ParameterizedTest
    @MethodSource("sqlTestPaths")
    fun testSQL(sqlFile: String) {
        pg.client.runTestFile(sqlFile)
    }
}

