import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("wrapper")
}

group = "dev.tricked"
version = "0.1.0"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}


dependencies {
    implementation("dev.hollowcube:minestom-ce:8715f4305d")
    runtimeOnly("ch.qos.logback:logback-core:1.3.5")
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}

sourceSets {
    getByName("main") {
        kotlin.srcDirs("src/main/kotlin")
    }
}

tasks {
    application {
        mainClass.set("dev.tricked.subnauticraft.MainKt")
    }
    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}

tasks.withType<Jar> {
    manifest {
        // Change this to your main class
        attributes["Main-Class"] = "dev.tricked.subnauticraft.MainKt"
    }
}