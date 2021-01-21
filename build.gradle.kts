plugins {
    java
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.3.0"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = sourceCompatibility
}

group = "me.nahu.itemshop"
version = "0.1.0"

repositories {
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }

    maven {
        name = "spigotmc"
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    maven {
        name = "papermc"
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }

    maven {
        name = "aikar-repo"
        url = uri("https://repo.aikar.co/content/groups/aikar/")
    }

    maven {
        name = "themoep-repo"
        url = uri("https://repo.minebench.de/")
    }

    maven {
        name = "codemc-repo"
        url = uri("https://repo.codemc.io/repository/maven-snapshots/")
    }

    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }

    mavenCentral()
    jcenter()
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:20.1.0")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    implementation("co.aikar:acf-bukkit:0.5.0-SNAPSHOT")
    implementation("de.themoep:minedown:1.6.2-SNAPSHOT")
    implementation("net.wesjd:anvilgui:1.4.0-SNAPSHOT")
    implementation(files("libs/InventoryGui.jar"))
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    arrayOf(
        "co.aikar.commands",
        "co.aikar.locales",
        "me.tom.sparse.spigot.chat",
        "de.themoep.minedown",
        "net.wesjd.anvilgui"
    ).forEach { relocate(it, "${project.group}.shadow.$it") }
}

bukkit {
    name = "ItemShop"
    description = "Very nice items shop plugin!"
    main = "me.nahu.itemshop.ItemShopPlugin"
    authors = listOf("NahuLD")
}