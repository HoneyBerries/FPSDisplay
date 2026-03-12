pluginManagement {
	repositories {
		maven {
			name = "Fabric"
			url = uri("https://maven.fabricmc.net/")
		}
		mavenCentral()
		gradlePluginPortal()
	}

	resolutionStrategy {
		eachPlugin {
			if (requested.id.id == "net.fabricmc.fabric-loom-remap") {
				useModule("net.fabricmc:fabric-loom:${providers.gradleProperty("loom_version").get()}")
			}
		}
	}

	plugins {
		id("net.fabricmc.fabric-loom-remap") version providers.gradleProperty("loom_version").get()
	}
}
