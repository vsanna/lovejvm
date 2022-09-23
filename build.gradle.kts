plugins {
    id("java")
    id("com.diffplug.gradle.spotless") version "4.5.1"
}

group 'dev.ishikawa'
version '1.0-SNAPSHOT'

repositories {
    mavenCentral()
}

dependencies {
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.8.1'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.8.1'
}

test {
    useJUnitPlatform()
}

spotless {
    java {
        target(
            "src/**/*.java",
        )
        removeUnusedImports()
        googleJavaFormat("1.8").aosp()
        importOrder()
        trimTrailingWhitespace()
        endWithNewline()
    }
}
