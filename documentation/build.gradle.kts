plugins {
    id("com.eden.orchidPlugin") version Versions.com_eden_orchidplugin_gradle_plugin
}

configurations {
    val orchidRuntimeOnly by getting {
        resolutionStrategy {
            force(Libs.plantuml)
        }
    }
    create("plantuml") {
        isTransitive = true
    }
}

dependencies {
    orchidRuntimeOnly(Libs.orchiddocs)
    orchidRuntimeOnly(Libs.orchidkotlindoc)
    orchidRuntimeOnly(Libs.orchidplugindocs)
//    orchidRuntimeOnly(Libs.orchidasciidoc)
//    orchidRuntimeOnly(Libs.orchiddiagrams)

    val plantuml by configurations.getting

    plantuml(Libs.plantuml)
}

repositories {
    mavenCentral()
    jcenter()
    maven(Repos.kotlinx)
}

// env ORG_GRADLE_PROJECT_orchidBaseUrl
val orchidBaseUrl = getPropertyOrWarnForAbsence("orchidBaseUrl")

orchid {
    theme = "Editorial"
    baseUrl = orchidBaseUrl
    version = rootProject.version.toString()
    args = listOf("--experimentalSourceDoc")
}

fun File.changeExtension(ext: String): File {
    return File(parentFile, "$nameWithoutExtension.$ext")
}

val plantUmlFiles = fileTree("$projectDir/src/orchid/resources/assets/diagrams")
    .also { it.include("**/*.puml").include("**/*.uml") }

if (!plantUmlFiles.isEmpty) {
    val generateUmlDiagramsInSvg by tasks.creating(JavaExec::class) {
        inputs.files(plantUmlFiles)
        outputs.files(plantUmlFiles
            .map { it.changeExtension("svg").absolutePath }
            .map { it.replace("diagrams", "generated") }
            .map(::File))
        classpath = configurations.getByName("plantuml")
        main = "net.sourceforge.plantuml.Run"
        args("-tsvg", "-o", "$projectDir/src/orchid/resources/assets/generated")
        args(plantUmlFiles.map { it.absolutePath })
    }
    tasks.getByName("orchidClasses").dependsOn(generateUmlDiagramsInSvg)
}