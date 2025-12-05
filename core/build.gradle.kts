plugins {
    id("java")
    id("io.quarkus") version "3.29.4"
}

repositories {
    mavenCentral()
    mavenLocal()
}

group = "com.honeybee"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    // Quarkus core dependencies
    implementation(enforcedPlatform("io.quarkus.platform:quarkus-bom:3.29.4"))
    implementation("io.quarkus:quarkus-arc")
    implementation("io.quarkus:quarkus-hibernate-reactive-panache")
    implementation("io.quarkus:quarkus-rest") // For JAX-RS Provider support
    
    // Share module for common enums/constants
    implementation(project(":share"))
}
