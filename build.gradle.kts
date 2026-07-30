plugins {
    java
    id("io.quarkus")
}

version = "0.1.0"

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

repositories {
    mavenCentral()
}

val lwjglVersion = "3.3.4"
val jomlVersion = "1.10.8"

val lwjglNatives = Pair(
    System.getProperty("os.name")!!,
    System.getProperty("os.arch")!!
).let { (name, arch) ->
    when {
        arrayOf("Linux", "SunOS", "Unit").any { name.startsWith(it) } ->
            if (arrayOf("arm", "aarch64").any { arch.startsWith(it) })
                "natives-linux${if (arch.contains("64") || arch.startsWith("armv8")) "-arm64" else "-arm32"}"
            else if (arch.startsWith("ppc")) "natives-linux-ppc64le"
            else if (arch.startsWith("riscv")) "natives-linux-riscv64"
            else "natives-linux"
        arrayOf("Mac OS X", "Darwin").any { name.startsWith(it) } ->
            "natives-macos${if (arch.startsWith("aarch64")) "-arm64" else ""}"
        arrayOf("Windows").any { name.startsWith(it) } ->
            if (arch.contains("64"))
                "natives-windows${if (arch.startsWith("aarch64")) "-arm64" else ""}"
            else "natives-windows-x86"
        else -> throw Error("Unrecognized platform: $name / $arch")
    }
}

dependencies {
    implementation(enforcedPlatform("$quarkusPlatformGroupId:$quarkusPlatformArtifactId:$quarkusPlatformVersion"))
    implementation("io.quarkus:quarkus-arc")

    implementation("jakarta.json:jakarta.json-api:2.1.3")
    implementation("org.eclipse.parsson:parsson:1.1.6")

    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    implementation("org.lwjgl:lwjgl")
    implementation("org.lwjgl:lwjgl-glfw")
    implementation("org.lwjgl:lwjgl-opengl")
    implementation("org.lwjgl:lwjgl-stb")
    runtimeOnly("org.lwjgl:lwjgl::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-glfw::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-opengl::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-stb::$lwjglNatives")

    implementation("org.joml:joml:$jomlVersion")

    testImplementation("io.quarkus:quarkus-junit5")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<Javadoc> {
    options {
        this as StandardJavadocDocletOptions
        addStringOption("Xdoclint:none", "-quiet")
        windowTitle = "FORE Engine ${project.version} API"
        docTitle = "FORE Engine ${project.version}"
        header = "<b>FORE Engine</b>"
        links("https://docs.oracle.com/en/java/javase/21/docs/api/")
        links("https://javadoc.io/doc/org.joml/joml/1.10.8/")
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf("-parameters"))
}

val isMacOS = System.getProperty("os.name").lowercase().contains("mac")

tasks.register<Exec>("runEngine") {
    group = "application"
    description = "Build and run the FORE engine"
    dependsOn("quarkusBuild")

    val javaHome = System.getProperty("java.home")
    executable("$javaHome/bin/java")

    val execArgs = mutableListOf<String>()
    if (isMacOS) {
        execArgs.add("-XstartOnFirstThread")
    }
    execArgs.add("-jar")
    execArgs.add(layout.buildDirectory.file("fore-engine-${project.version}-runner.jar").get().asFile.absolutePath)

    args(execArgs)
}
