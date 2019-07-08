import com.jfrog.bintray.gradle.BintrayExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.dokka") version "0.9.18"
    id("com.jfrog.bintray") version "1.8.4"
    java
    `maven-publish`
    kotlin("jvm") version "1.3.31"
}

group = "me.schlaubi"
version = "1.2"

repositories {
    mavenCentral()
    jcenter()
    maven { url = uri("http://oss.sonatype.org/content/repositories/snapshots") }
}


dependencies {

    // MongoDB
    compile("org.mongodb", "mongodb-driver-sync", "3.10.1")

    // For guava caches
    implementation("com.google.guava:guava:27.1-jre")

    // Kotlin
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    // Tests
    testCompile("org.slf4j", "slf4j-nop", "1.7.26") // Mute mongod logs
    testCompile("junit", "junit", "4.12")

}

val sourcesJar by tasks.creating(Jar::class)
val dokkaJar by tasks.creating(Jar::class)

bintray {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_KEY")
    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = "maven"
        name = "mongoutils"
        userOrg = "hawk"
        setLicenses("GPL-3.0")
        vcsUrl = "https://github.com/DRSchlaubi/mongoutils.git"
        version(delegateClosureOf<BintrayExtension.VersionConfig> {
            name = project.version as String
        })
    })
    this.setPublications("mavenJava")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(sourcesJar)
            artifact(dokkaJar)
            groupId = project.group as String
            artifactId = project.name
            version = project.version as String
        }
    }
}

tasks {
    dokka {
        outputDirectory = "${project.projectDir}/docs"
        // Oracle broke it
        noJdkLink = true
        reportUndocumented = true
        impliedPlatforms = mutableListOf("JVM")

    }

    "sourcesJar"(Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets["main"].allSource)
    }
    "dokkaJar"(Jar::class) {
        group = JavaBasePlugin.DOCUMENTATION_GROUP
        archiveClassifier.set("javadoc")
        from(dokka)
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}