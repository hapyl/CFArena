plugins {
    `java-library`
    `maven-publish`

    id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

repositories {
    mavenCentral()

    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        url = uri("https://maven.pkg.github.com/hapyl/EternaAPI")
        credentials {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}

dependencies {
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")

    api(libs.org.mongodb.mongo.java.driver)
    implementation("me.hapyl:eternaapi:5.2.2-SNAPSHOT")
}

group = "me.hapyl"
version = "2.50.0-SNAPSHOT"
description = "CFArena"
java.sourceCompatibility = JavaVersion.VERSION_21

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    named<ProcessResources>("processResources") {
        filteringCharset = "UTF-8"
        duplicatesStrategy = DuplicatesStrategy.INCLUDE

        from(sourceSets.main.get().resources.srcDirs) {
            include("plugin.yml")
            expand("version" to project.version)
        }
    }

    runServer {
        minecraftVersion("1.21.11")
    }
}