plugins {
    java
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

    // Optional Spring Boot integration. The root library stays dependency-free.
    implementation(platform("org.springframework.boot:spring-boot-dependencies:4.1.2"))
    implementation("org.springframework.boot:spring-boot-autoconfigure")

    testImplementation(project(":examples"))
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
        showStandardStreams = false
    }
}

tasks.register<JavaExec>("runSpringRecsys") {
    group = "application"
    description = "Run the recsys demo as a Spring Boot application."
    classpath = sourceSets["test"].runtimeClasspath
    mainClass.set("io.lark.spring.example.SpringRecSysDemo")
    jvmArgs("--add-modules", "jdk.incubator.vector")
}
