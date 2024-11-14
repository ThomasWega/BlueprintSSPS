plugins {
    `java-library`
    `maven-publish`
    id("io.freefair.lombok") version "8.10"
    id("com.gradleup.shadow") version "8.3.3"
    id("io.papermc.paperweight.userdev") version "1.7.3"
}

group = "com.eteirnum"
version = "0.1-SNAPSHOT"
description = "Eteirnum RPG server Core"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
}

dependencies {
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
    // NEED TO HAVE INSTALLED ON LOCAL!
    implementation("com.eteirnum:EteirnumToolkit:0.1-SNAPSHOT:dev-all")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group.toString()
            artifactId = rootProject.name
            version = version
            from(components["java"])
        }
    }
}

tasks {
    shadowJar {
        minimize()
    }
    assemble {
        dependsOn(reobfJar)
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()

        options.release.set(21)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }
}