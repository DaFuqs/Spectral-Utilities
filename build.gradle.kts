import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.3.0"
    id ("fabric-loom") version "1.11-SNAPSHOT"
    `maven-publish`
    java
}

version = property("mod_version")!!
group = property("maven_group")!!

sourceSets{
    main{
        resources{
            srcDirs("src/main/generated")//, "src/main/resources")
        }
    }
}

loom{
    runs{

        create("Data Generation"){

            client()
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.output-dir=${file("src/main/generated")}")
            vmArg("-Dfabric-api.datagen.strict_validation")
            vmArg("-Dfabric-api.datagen.modid=${project.property("modid")}")



            ideConfigGenerated(true)
            runDir = "build/datagen"
        }
    }
}

repositories {
    maven { setUrl("https://maven.is-immensely.gay/releases") }
    // Revelationary, Fractal, AEA, Arrowhead, Dimensional Reverb...
    maven { setUrl("https://maven.shedaniel.me/") } // Cloth Config, REI
    maven { setUrl("https://maven.terraformersmc.com/") } // Modmenu, EMI
    maven { setUrl("https://maven.nucleoid.xyz/") } // Common Protection API
    maven { setUrl("https://maven.blamejared.com") } // JEI
    maven { setUrl("https://maven.ladysnake.org/releases") } // Cardinal Components API
    maven { setUrl("https://maven.jamieswhiteshirt.com/libs-release/") } // Entity Reach Attribute
    maven { setUrl("https://api.modrinth.com/maven") } // Colorful Hearts, idwtialsimmoedm
    maven { setUrl("https://cfa2.cursemaven.com") }
    maven { setUrl("https://mvn.devos.one/releases/") } // Porting Lib
    maven { setUrl("https://repo.unascribed.com") } // Ears API
    maven { setUrl("https://dl.cloudsmith.io/public/klikli-dev/mods/maven/") } // Modonomicon
    maven { setUrl("https://maven.wispforest.io/releases") }
    maven {
        name = "ParchmentMC"
        setUrl("https://maven.parchmentmc.org")
    }
    maven { setUrl("https://maven.kyrptonaught.dev") } // CustomPortalAPI as requirement for Starry Skies
    mavenCentral()
}

dependencies {
    minecraft ("com.mojang:minecraft:${property("minecraft_version")}")
    mappings (loom.layered() {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${property("parchment_mappings")}@zip")
    })

    modImplementation ("net.fabricmc:fabric-loader:${property("loader_version")}")
    modImplementation ("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")

    modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")

    modImplementation("de.dafuqs:spectrum:${property("spectrum_version")}") {
        exclude(group = "com.unascribed:ears-api")
        exclude(group = "maven.modrinth:colorful-hearts")
    }

    modImplementation("maven.modrinth:exclusions-lib:${property("exclusionslib_version")}")
    modImplementation("de.dafuqs:revelationary:${property("revelationary_version")}")
    modImplementation("de.dafuqs:additionalentityattributes:${property("additional_entity_attributes_version")}")
    modImplementation("de.dafuqs:arrowhead:${property("arrowhead_version")}")
    modImplementation("de.dafuqs:reverb:${property("dimensional_reverb_version")}")
    modImplementation("de.dafuqs:fractal:${property("fractal_version")}")

    modImplementation("com.klikli_dev:modonomicon-${property("minecraft_version")}-fabric:${property("modonomicon_version")}") { exclude(group = "com.klikli_dev") }
    modImplementation("dev.emi:trinkets:${property("trinkets_version")}")

    listOf("base", "entity", "item", "level", "scoreboard").forEach{
        modImplementation("org.ladysnake.cardinal-components-api:cardinal-components-$it:${property("cca_version")}")
        include("org.ladysnake.cardinal-components-api:cardinal-components-$it:${property("cca_version")}")
    }

    /*modImplementation include("org.ladysnake.cardinal-components-api:cardinal-components-base:${property("cca_version")}")
    modImplementation include("org.ladysnake.cardinal-components-api:cardinal-components-entity:${property("cca_version")}")
    modImplementation include("org.ladysnake.cardinal-components-api:cardinal-components-item:${property("cca_version")}")
    modImplementation include("org.ladysnake.cardinal-components-api:cardinal-components-level:${property("cca_version")}")
    modImplementation include("org.ladysnake.cardinal-components-api:cardinal-components-scoreboard:${property("cca_version")}")
    */

    modApi("me.shedaniel.cloth:cloth-config-fabric:${property("cloth_config_version")}") { exclude(group = "net.fabricmc.fabric-api") }
    modApi("com.terraformersmc:modmenu:${property("modmenu_version")}")

    val rei_version = property("rei_version")

    // https://github.com/shedaniel/RoughlyEnoughItems
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-fabric:${rei_version}")

    val emi_version = property("emi_version")

    // https://github.com/emilyploszaj/emi
    modCompileOnly("dev.emi:emi-fabric:${emi_version}")

    when ("${property("recipe_viewer")}".toLowerCase()) {
        "rei" -> modLocalRuntime("me.shedaniel:RoughlyEnoughItems-fabric:$rei_version")
        "emi" -> modLocalRuntime("dev.emi:emi-fabric:${emi_version}")
        "disabled" -> Unit
        else -> println("Unknown recipe viewer specified: ${property("recipe_viewer")}. Must be EMI, REI or disabled.")
    }
}

val archivesBaseName = property("archives_base_name")

tasks {
    processResources {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        inputs.property("version", project.version)
        inputs.property("archives_base_name", archivesBaseName)
        //inputs.property("minecraft_version", property("minecraft_version"))
        //inputs.property("loader_version", property("loader_version"))
        //filteringCharset = "UTF-8"

        filesMatching("fabric.mod.json") {
            expand(getProperties())
            expand(
                mutableMapOf(
                    "version" to project.version,
                    //"minecraft_version" to property("minecraft_version"),
                    //"loader_version" to property("loader_version")
                )
            )
        }
    }
    jar {

        from("LICENSE") {
            rename { "${it}_${archivesBaseName}"}
        }
    }


    // configure the maven publication
    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                artifact(remapJar) {
                    builtBy(remapJar)
                }
                artifact(kotlinSourcesJar) {
                    builtBy(remapSourcesJar)
                }
            }
        }

        // See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
        repositories {
            /*maven {
                name = "sapphoCompany"
                url = "https://maven.is-immensely.gay/releases"
                credentials {
                    username = "dafuqs"
                    password = System.getenv("MAVEN_PASS")
                }
            }*/
        }
    }

    compileJava{
        targetCompatibility = "21"
        sourceCompatibility = "21"
    }

    compileKotlin{
        compilerOptions.jvmTarget = JvmTarget.JVM_21
        compilerOptions.freeCompilerArgs.set(listOf("-Xcontext-parameters"))
    }
}

/*tasks.withType(JavaCompile).configureEach {
    it.options.release = 21
}*/

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

