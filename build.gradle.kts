import org.gradle.api.plugins.*
import org.gradle.api.artifacts.*
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.internal.file.pattern.PatternMatcherFactory.compile
import org.gradle.script.lang.kotlin.*

plugins {
    `java-library`
}

version = "0.8.1"

repositories.jcenter()

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.2.21")
    testImplementation("junit:junit:4.12")
}

buildscript {
    repositories.mavenCentral()
    dependencies.classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.2.21")
}
