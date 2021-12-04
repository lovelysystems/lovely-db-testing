package com.lovelysystems.db.testing

import org.testcontainers.containers.Container
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy
import org.testcontainers.utility.DockerImageName
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream
import kotlin.io.path.absolutePathString
import kotlin.streams.toList

class PGClientContainer(
    val testDir: Path?,
    imageName: String,
    val devDir: Path? = null,
    val defaultDB: String = "postgres",
    val testFilePattern: String = "**/*.sql",
    val configureBlock: PGClientContainer.() -> Unit = {}
) :
    GenericContainer<PGClientContainer>(DockerImageName.parse(imageName)) {
    override fun configure() {
        withEnv(
            mapOf(
                "PGUSER" to "postgres",
                "PGPASSWORD" to "postgres",
                "PGHOST" to "postgres"
            )
        )
        // keep alive
        withCreateContainerCmdModifier {
            it.withEntrypoint("tail", "-f", "/dev/null")
        }
        if (devDir != null) {
            withFileSystemBind(devDir.absolutePathString(), "/pgdev")
        }
        if (testDir != null) {
            withFileSystemBind(testDir.absolutePathString(), "/tests")
        }
        this.apply(configureBlock)
    }

    fun psql(vararg args: String): String {
        val res = execInContainer(
            "psql",
            "-v", "ON_ERROR_STOP=1",
            *args
        )!!
        if (res.exitCode != 0) {
            throw PSQLException(res)
        }
        return res.stdout
    }

    fun runTestFile(path: String, dbName: String = defaultDB): String {
        return psql("-f", "/tests/$path", dbName)
    }

    fun runFiles(vararg fileNames: String, dbName: String = defaultDB): String {
        return psql(*fileNames.flatMap { listOf("-f", it) }.toTypedArray(), dbName)
    }

    fun testPaths(): List<Path> {
        return testDir?.listRecursiveEntries(testFilePattern)?.toList() ?: emptyList()
    }
}

class PGServerContainer(imageName: String, val configureBlock: PGServerContainer.() -> Unit) :
    GenericContainer<PGServerContainer>(DockerImageName.parse(imageName)) {

    override fun configure() {
        withNetworkAliases("postgres")
        withTmpFs(mapOf("/var/lib/postgresql/data" to "rw"))
        withEnv(
            mapOf(
                "PGUSER" to "postgres",
                "POSTGRES_PASSWORD" to "postgres",
            )
        )
        setWaitStrategy(LogMessageWaitStrategy().withRegEx(".*database system is ready to accept connections.*"))
        withCommand("-c", "fsync=off")
        this.apply(configureBlock)
    }
}

@Suppress("CanBeParameter", "MemberVisibilityCanBePrivate")
class PSQLException(val execResult: Container.ExecResult) :
    RuntimeException("${execResult.exitCode}\n${execResult.stderr}")

fun Path.listRecursiveEntries(pattern: String): Stream<Path> {
    val matcher = if (pattern.startsWith("glob:") || pattern.startsWith("regex:")) {
        FileSystems.getDefault().getPathMatcher(pattern)
    } else {
        FileSystems.getDefault().getPathMatcher("glob:$pattern")
    }
    return Files.walk(this).map { this.relativize(it) }.filter { matcher.matches(it) }
}