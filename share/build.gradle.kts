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
