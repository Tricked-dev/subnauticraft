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
    maven(url = "https://repo.spongepowered.org/maven")
    maven(url = "https://repo.minestom.com/repository/maven-public/")
    maven(url = "https://repo.velocitypowered.com/snapshots/")
}


dependencies {
    implementation("dev.hollowcube:minestom-ce:8715f4305d")
    implementation("dev.hollowcube:schem:1.0.0")
    implementation("com.github.EmortalMC:Rayfast:7975ac5e4c")
    implementation("com.github.Tricked-dev:Particable:9d4c3288d5")
//    implementation("com.github.Project-Cepi:KStom:latest")
//    implementation("com.github.Project-Cepi:Particable:acea414be2")
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