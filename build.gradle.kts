import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.31"
}

group = "me.schlaubi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("http://oss.sonatype.org/content/repositories/snapshots") }
}

dependencies {

    // MongoDB
    compile("org.mongodb", "mongodb-driver-sync", "3.10.1")

    // Kotlin
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    // Tests
    testCompile("org.slf4j", "slf4j-nop", "1.7.26") // Mute mongod logs
    testCompile("junit", "junit", "4.12")
    testCompile("de.flapdoodle.embed", "de.flapdoodle.embed.mongo", "2.1.2-SNAPSHOT")

}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}