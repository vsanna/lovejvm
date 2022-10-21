plugins {
    id("java")
    id("com.diffplug.spotless") version "6.11.0"
}

group "dev.ishikawa"
version "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:16.0.2")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    testImplementation("org.assertj:assertj-core:3.23.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

spotless {
    java {
        target(
            "src/**/*.java",
            "libs/custom/java/**/*.java"
        )
        removeUnusedImports()
        googleJavaFormat("1.8")
        importOrder()
        trimTrailingWhitespace()
        endWithNewline()
        toggleOffOn("/*", "*/")
    }
}
