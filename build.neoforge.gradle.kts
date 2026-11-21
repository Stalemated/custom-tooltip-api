import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.gradle.ext.Gradle
import org.jetbrains.gradle.ext.ProjectSettings
import org.jetbrains.gradle.ext.RunConfigurationContainer

plugins {
    `java-library`
    id("net.neoforged.moddev")
    id("neoforge-mutex")
    id("mod-conventions")
    id("mod-publishing")
    id("slib")
    id("dev-mods")
}

repositories {
    exclusiveContent {
        forRepository { maven("https://cursemaven.com") { name = "CurseMaven" } }
        filter { includeGroup("curse.maven") }
    }
    exclusiveContent {
        forRepository { maven("https://maven.terraformersmc.com/releases") { name = "TerraformersMC EMI" } }
        filter { includeGroup("dev.emi") }
    }
}

dependencies {
    testRuntimeOnly(files(sourceSets.main.get().runtimeClasspath))
    testRuntimeOnly("org.slf4j:slf4j-api:${property("deps.slf4j")}")
}

neoForge {
    version = property("deps.neo_loader") as String

    mods {
        register(property("mod.id") as String) {
            sourceSet(sourceSets.main.get())
        }
    }

    runs {
        register("client") { client() }
        register("server") { server() }

        configureEach {
            gameDirectory = rootProject.file("run/${sc.current.project}")
            disableIdeRun()
        }
    }
}

publishMods {
    file = tasks.jar.flatMap { it.archiveFile }
}

if (providers.systemProperty("idea.sync.active").orNull == "true") {
    val ideaSettings = (rootProject.the<IdeaModel>().project as ExtensionAware).the<ProjectSettings>() as ExtensionAware
    val nodePath = path
    neoForge.runs.configureEach {
        val side = name.replaceFirstChar(Char::uppercase)
        ideaSettings.the<RunConfigurationContainer>().register<Gradle>("Minecraft $side ($nodePath)") {
            taskNames = listOf("$nodePath:run$side")
            projectPath = rootDir.invariantSeparatorsPath
        }
    }
}

tasks {
    processResources {
        exclude("fabric.mod.json", "META-INF/mods.toml", "pack.mcmeta", "*-fabric.mixins.json")

        if (sc.current.parsed < "1.20.5") {
            filesMatching("META-INF/neoforge.mods.toml") { path = "META-INF/mods.toml" }
        }
    }

    named("createMinecraftArtifacts") {
        dependsOn("stonecutterGenerate")
    }

    named<Copy>("buildAndCollect") {
        from(jar.flatMap { it.archiveFile }, named<Jar>("sourcesJar").flatMap { it.archiveFile })
    }
}
