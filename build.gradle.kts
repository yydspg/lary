plugins {
    java
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
