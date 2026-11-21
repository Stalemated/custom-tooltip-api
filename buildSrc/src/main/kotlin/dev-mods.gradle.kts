import dev.kikugie.stonecutter.build.StonecutterBuildExtension

val sc = extensions.getByType<StonecutterBuildExtension>()
val node = sc.current.project
val loader = node.substringAfter('-')

val runtimeGroups = sc.properties.rawOrNull("runtime")?.asMap().orEmpty()

fun isTurnedOn(mod: String): Boolean = requireNotNull(sc.properties.getOrNull<Boolean>("dev.$mod")) {
    "runtime.$mod in $node needs a dev.$mod = true or false flag"
}

fun coordinate(mod: String, dep: String): String = requireNotNull(sc.properties.getOrNull<String>("deps.$dep")) {
    "runtime.$mod in $node lists $dep, but $node has no deps.$dep"
}

val coordinatesByMod = runtimeGroups.filterKeys(::isTurnedOn).mapValues { (mod, group) ->
    group.asList().map { coordinate(mod, it.toString()) }
}

val versionClashes = coordinatesByMod
    .flatMap { (mod, coordinates) -> coordinates.map { mod to it } }
    .groupBy { (_, coordinate) -> coordinate.substringBeforeLast(':') }
    .filterValues { requests -> requests.map { it.second }.distinct().size > 1 }

require(versionClashes.isEmpty()) {
    versionClashes.entries.joinToString("; ", postfix = ". Turn one of those dev flags off in stonecutter.properties.toml") { (artifact, requests) ->
        val versions = requests.map { it.second.substringAfterLast(':') }.distinct()
        val flags = requests.map { "dev.${it.first}" }.distinct()
        "$node gets $artifact in versions $versions from $flags"
    }
}

fun loadAsProductionJars(coordinates: List<String>) {
    val productionJars = configurations.create("devModJars") {
        isTransitive = false
        isCanBeConsumed = false
    }
    coordinates.forEach { dependencies.add(productionJars.name, it) }

    val productionJarsDir = layout.buildDirectory.dir("dev-mods")
    val syncProductionJars = tasks.register<Sync>("syncDevMods") {
        description = "Syncs Test Mod jar files."
        from(productionJars)
        into(productionJarsDir)
    }

    tasks.withType<JavaExec>().matching { it.name == "runClient" }.configureEach {
        dependsOn(syncProductionJars)
        systemProperty("fabric.addMods", productionJarsDir.get().asFile.absolutePath)
    }
}

fun addRemapped(configuration: String, coordinates: List<String>) {
    coordinates.forEach { dependencies.add(configuration, it) { isTransitive = false } }
}

val devModLoaders: Map<String, (List<String>) -> Unit> = mapOf(
    "fabric" to ::loadAsProductionJars,
    "forge" to { coordinates -> addRemapped("modRuntimeOnly", coordinates) },
    "neoforge" to { coordinates -> addRemapped("runtimeOnly", coordinates) },
)

val loadDevMods = requireNotNull(devModLoaders[loader]) {
    "loader must be one of ${devModLoaders.keys.toList()}, got '$loader'"
}
loadDevMods(coordinatesByMod.values.flatten().distinct())
