plugins {
    java
    application
}

group = "io.lark"
version = rootProject.version

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

dependencies {
    implementation(project(":"))

    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass.set("io.lark.examples.recsys.RecSysDemo")
    applicationDefaultJvmArgs = listOf("--add-modules", "jdk.incubator.vector")
}

/**
 * The JVM prints "WARNING: Using incubator modules: ..." when the incubator
 * module is resolved. Filter that exact line for Gradle-launched examples.
 */
class IncubatorWarningFilter(
    private val delegate: java.io.OutputStream,
    private val blocked: String
) : java.io.OutputStream() {
    private val line = StringBuilder()

    override fun write(b: Int) {
        if (b == 10) emitLine() else line.append(b.toChar())
    }

    override fun flush() {
        if (line.isNotEmpty()) emitLine()
        delegate.flush()
    }

    override fun close() {
        flush()
    }

    private fun emitLine() {
        val text = line.toString()
        if (!text.contains(blocked)) {
            delegate.write((text + "\n").toByteArray(Charsets.UTF_8))
            delegate.flush()
        }
        line.setLength(0)
    }
}

val incubatorWarning = "WARNING: Using incubator modules: jdk.incubator.vector"

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf(
        "-Xlint:all",
        "-Xlint:-incubating",
        "-parameters",
        "--add-modules", "jdk.incubator.vector"
    ))
}

tasks.withType<JavaExec>().configureEach {
    jvmArgs("--add-modules", "jdk.incubator.vector")
    errorOutput = IncubatorWarningFilter(System.err, incubatorWarning)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    jvmArgs("--add-modules", "jdk.incubator.vector")
    testLogging {
        events("passed", "failed", "skipped")
        showStandardStreams = false
    }
}

tasks.register<JavaExec>("runSimple") {
    group = "application"
    description = "Run the Node-layer DAG example."
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("io.lark.examples.SimplePipeline")
}

tasks.register<JavaExec>("runOp") {
    group = "application"
    description = "Run the Op/Aspect DAG example."
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("io.lark.examples.OpPipeline")
}

tasks.register<JavaExec>("runRecsys") {
    group = "application"
    description = "Run the search/recommendation metric demo."
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("io.lark.examples.recsys.RecSysDemo")
}

tasks.register<JavaExec>("runRecsysBenchmark") {
    group = "application"
    description = "Run the warmed-up search/recommendation benchmark."
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("io.lark.examples.recsys.RecSysBenchmark")
    listOf("rows", "warmup", "iterations", "workers").forEach { name ->
        (project.findProperty("lary.bench.$name") as String?)?.let {
            systemProperty("lary.bench.$name", it)
        }
    }
}
