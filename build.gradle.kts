plugins {
    java
    application
}

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
        "-parameters",
        "--add-modules", "jdk.incubator.vector"
    ))
}

tasks.withType<JavaExec>().configureEach {
    jvmArgs("--add-modules", "jdk.incubator.vector")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    jvmArgs("--add-modules", "jdk.incubator.vector")
    testLogging {
        events("passed", "failed", "skipped")
        showStandardStreams = true
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
