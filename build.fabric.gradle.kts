plugins {
    id("dev.kikugie.loom-back-compat")
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
    minecraft("com.mojang:minecraft:${sc.current.version}")
    loomx.applyMojangMappings()

    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
    listOf("tierify", "necronomicon").forEach { mod -> findProperty("deps.$mod")?.let { modCompileOnly(it as String) } }

    testRuntimeOnly(files(sourceSets.main.get().compileClasspath))
}

loom {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")

    decompilerOptions.named("vineflower") {
        options.put("mark-corresponding-synthetics", "1")
    }

    runConfigs.all {
        preferGradleTask = true
        generateRunConfig = true
        runDirectory = rootProject.file("run/${sc.current.project}")
        jvmArguments.add("-Dmixin.debug.export=true")
    }
}

publishMods {
    file = loomx.modJar.flatMap { it.archiveFile }
}

tasks {
    processResources {
        exclude("META-INF/neoforge.mods.toml", "META-INF/mods.toml", "pack.mcmeta")
    }

    named<Copy>("buildAndCollect") {
        from(loomx.modJar.flatMap { it.archiveFile }, loomx.modSourcesJar.flatMap { it.archiveFile })
    }
}
