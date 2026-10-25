plugins {
    java
    application
}

/**
 * The JVM itself prints "WARNING: Using incubator modules: ..." when an
 * incubator module is resolved, and there is no JVM flag to disable it.
 * Filter that exact stderr line for Gradle-launched examples.
 */
class IncubatorWarningFilter(
    private val delegate: java.io.OutputStream,
    private val blocked: String
) : java.io.OutputStream() {
    private val line = StringBuilder()

    override fun write(b: Int) {
        if (b == 10) {
            emitLine()
        } else {
            line.append(b.toChar())
        }
    }

    override fun flush() {
        if (line.isNotEmpty()) {
            emitLine()
        }
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

group = "io.lark"
version = "0.2.0"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
    withSourcesJar()
}

application {
    mainClass.set("io.lark.examples.SimplePipeline")
    applicationDefaultJvmArgs = listOf("--add-modules", "jdk.incubator.vector")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

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
        // The incubator warning is emitted by the test JVM itself. Hide raw
        // test-process streams; failures still show in reports and events.
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

tasks.register<JavaExec>("runColumn") {
    group = "application"
    description = "Run the column feed/compute/fetch example (Vector API)."
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("io.lark.examples.ColumnPipelineExample")
}
