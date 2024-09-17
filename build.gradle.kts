plugins {
    id("java")
    id("io.github.patrick.remapper") version "1.4.2"
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

group = "anvil.fix"
version = "2.0"

tasks.create<Copy>("copyShadowOutputToLocalServer") {
    from(tasks.jar)
    destinationDir = file("C:\\Users\\Kubia\\Desktop\\PAPER SEX\\plugins")
}

tasks.build {
    dependsOn(tasks["copyShadowOutputToLocalServer"])
}


//project.layout.buildDirectory.set(File("C:\\Users\\Kubia\\Desktop\\Server\\plugins"))


repositories {
    mavenLocal()
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")

    maven("https://repo.codemc.io/repository/maven-releases/")

    //maven("https://repo.papermc.io/repository/maven-public/")
    //maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    //maven("https://mvnrepository.com/artifact/com.github.retrooper.packetevents/api")
    //maven("https://mvnrepository.com/artifact/com.github.retrooper.packetevents/spigot")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:1.20.1-R0.1-SNAPSHOT")
    compileOnly("com.github.retrooper:packetevents-spigot:2.4.0")
    //compileOnly("org.spigotmc:spigot:1.20.5-R0.1-SNAPSHOT:remapped-mojang")

    //compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    // https://mvnrepository.com/artifact/com.github.retrooper.packetevents/api
    //compileOnly("com.github.retrooper:packetevents-spigot:2.4.0")
    // https://mvnrepository.com/artifact/com.github.retrooper.packetevents/spigot
    //compileOnly("com.github.retrooper.packetevents:spigot:2.3.0")
}