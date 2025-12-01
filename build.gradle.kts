plugins {
    id("io.quarkus") version "3.29.4" apply false
}

allprojects {
    group = "com.honeybee"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
        mavenLocal()
    }
}

subprojects {
    if (path.startsWith(":services:")) {
        apply(plugin = "java")
        apply(plugin = "io.quarkus")

        configure<JavaPluginExtension> {
            sourceCompatibility = JavaVersion.VERSION_21
            targetCompatibility = JavaVersion.VERSION_21
        }

        tasks.withType<JavaCompile> {
            options.encoding = "UTF-8"
            options.compilerArgs.add("-parameters")
        }

        tasks.withType<Test> {
            systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
        }
    }
}
