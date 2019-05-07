package org.openmicroscopy.utils


import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.openmicroscopy.extensions.IceExtension

@CompileStatic
class SliceHelper37 extends SliceHelper {

    private final Project project

    private IceExtension ice

    SliceHelper37(Project project, IceExtension ice) {
        this.project = project
        this.ice = ice
    }

    @Override
    void configure() {
        configureSliceDependencies()
        configureSliceSourceSets()
    }

    void configureSliceDependencies() {
        project.plugins.withType(JavaPlugin) {
            project.dependencies.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME,
                    "com.zeroc:icegrid:3.7.2")
            project.dependencies.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME,
                    "com.zeroc:icestorm:3.7.2")
        }
    }

    void configureSliceSourceSets() {
        project.plugins.withType(JavaPlugin) {
            JavaPluginConvention javaConvention = project.convention.getPlugin(JavaPluginConvention)
            SourceSet main = javaConvention.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
            main.java.srcDirs("src/main/ice37", ice.outputDir)
        }
    }


}
