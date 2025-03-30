plugins {
    `java-library`
    `maven-publish`
    id("io.freefair.lombok") version "8.10"
    id("io.papermc.paperweight.userdev") version "1.7.3"
    id("com.gradleup.shadow") version "8.3.3"
}

group = "me.wega"
version = "0.1-SNAPSHOT"
description = "toolkit"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://maven.enginehub.org/repo/") }
    maven { url = uri("https://repo.maven.apache.org/maven2/") }
    maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
    maven { url = uri("https://repo.codemc.io/repository/maven-public/") }
    maven { url = uri("https://hub.jeff-media.com/nexus/repository/jeff-media-public/") }
    maven { url = uri("https://repo.dmulloy2.net/repository/public/") }
    maven { url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/") }
}

dependencies {
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
    implementation("org.jetbrains:annotations:24.0.1")
    implementation("dev.jorel:commandapi-bukkit-shade:9.5.2")
    implementation("com.jeff-media:MorePersistentDataTypes:2.4.0")
    implementation("com.github.stefvanschie.inventoryframework:IF:0.10.17")
    implementation("com.github.fierioziy.particlenativeapi:ParticleNativeAPI-core:4.3.0")
    implementation("com.ezylang:EvalEx:3.3.0")
    compileOnly("com.fastasyncworldedit:FastAsyncWorldEdit-Bukkit:2.11.2") {
        isTransitive = false
    }
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.12-SNAPSHOT")
    compileOnly("net.kyori:adventure-text-minimessage:4.16.0")
    compileOnly("net.kyori:adventure-text-serializer-gson:4.16.0")
    compileOnly("net.kyori:adventure-text-serializer-plain:4.16.0")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.16.0")
    compileOnly("de.tr7zw:item-nbt-api-plugin:2.13.2")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.3.0")
    compileOnly("me.clip:placeholderapi:2.11.6")
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
        relocate("dev.jorel.commandapi", "me.wega.blueprint_toolkit.shaded.commandapi")
        relocate("com.jeff-media.MorePersistentDataTypes", "me.wega.blueprint_toolkit.shaded.more_persistent_data_types")
        relocate("com.github.stefvanschie.inventoryframework", "me.wega.blueprint_toolkit.shaded.inventoryframework")
        relocate("com.github.unldenis.holoeasy", "me.wega.blueprint_toolkit.shaded.holoeasy")
        relocate("com.github.fierioziy.particlenativeapi", "me.wega.blueprint_toolkit.shaded.particlenativeapi")
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
}
