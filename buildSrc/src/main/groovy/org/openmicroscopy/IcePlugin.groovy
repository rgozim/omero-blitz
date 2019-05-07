package org.openmicroscopy

import com.zeroc.gradle.icebuilder.slice.Java
import com.zeroc.gradle.icebuilder.slice.SliceExtension
import com.zeroc.gradle.icebuilder.slice.SlicePlugin
import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.TaskProvider
import org.openmicroscopy.blitz.BlitzPlugin
import org.openmicroscopy.blitz.extensions.BlitzExtension
import org.openmicroscopy.extensions.IceExtension
import org.openmicroscopy.tasks.IceDocsTask
import org.openmicroscopy.utils.SliceHelper

@CompileStatic
class IcePlugin implements Plugin<Project> {

    public static final String EXTENSION_ICE = "ice"

    public static final String TASK_COMPILE_ICE = "compileSlice"

    public static final String TASK_COMPILE_ICEDOC = "compileIcedoc"

    private Project project

    @Override
    void apply(Project project) {
        this.project = project

        project.pluginManager.apply(BlitzPlugin)
        project.pluginManager.apply(SlicePlugin)

        BlitzExtension blitz = project.extensions.getByType(BlitzExtension)

        // Create ice extension
        IceExtension ice =
                project.extensions.create(EXTENSION_ICE, IceExtension, project)

        // Set ice conventions based on BlitzExtension
        ice = project.extensions.getByType(IceExtension)
        ice.outputDir.set(blitz.outputDir.dir("java"))
        ice.iceSrcDir.set(blitz.outputDir.dir("slice"))

        configureSlice(ice)
        configureCompileSlice(ice)
        registerIceDocsTask(ice)

        // Set compileJava to depend on `compileIce`
        project.tasks.named(JavaPlugin.COMPILE_JAVA_TASK_NAME).configure {
            it.dependsOn(project.tasks.named(TASK_COMPILE_ICE))
        }
    }

    void configureSlice(IceExtension ice) {
        SliceHelper.newInstance(project, ice).configure()
    }

    void configureCompileSlice(IceExtension ice) {
        SliceExtension slice = project.extensions.getByType(SliceExtension)

        slice.output = ice.outputDir.asFile.get()

        slice.java.create("main", new Action<Java>() {
            @Override
            void execute(Java java) {
                java.include = [project.file(ice.iceSrcDir)]
                java.files = project.fileTree(ice.iceSrcDir).matching {
                    include: "**/*.ice"
                }
                java.args = "--tie"
            }
        })
    }

//    TaskProvider<IceJavaTask> registerIceJavaTask(IceExtension ice) {
//        project.tasks.register(TASK_COMPILE_ICE, IceJavaTask, new Action<IceJavaTask>() {
//            @Override
//            void execute(IceJavaTask t) {
//                t.setGroup("slice")
//                t.setSource(ice.iceSrcDir)
//                t.includeDirs.add(ice.iceSrcDir)
//                t.outputDir.set(ice.outputDir.dir("java"))
//                t.tie.set(true)
//            }
//        })
//    }

    TaskProvider<IceDocsTask> registerIceDocsTask(IceExtension ice) {
        project.tasks.register(TASK_COMPILE_ICEDOC, IceDocsTask, new Action<IceDocsTask>() {
            @Override
            void execute(IceDocsTask t) {
                t.source = ice.iceSrcDir
                t.includeDirs.add(ice.iceSrcDir)
                t.outputDir.set(ice.docsOutputDir)
            }
        })
    }

}
