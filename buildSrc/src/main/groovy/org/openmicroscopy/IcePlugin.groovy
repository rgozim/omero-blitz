package org.openmicroscopy

import com.zeroc.gradle.icebuilder.slice.SlicePlugin
import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.CopySpec
import org.gradle.api.file.ProjectLayout
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Zip
import org.openmicroscopy.api.extensions.ApiExtension
import org.openmicroscopy.api.extensions.SplitExtension
import org.openmicroscopy.blitz.BlitzPlugin
import org.openmicroscopy.extensions.IceExtension
import org.openmicroscopy.tasks.IceDocsTask

import javax.inject.Inject

@CompileStatic
class IcePlugin implements Plugin<Project> {

    public static final String EXTENSION_ICE = "ice"

    public static final String TASK_ZIP_ICEDOC = "zipIcedoc"

    public static final String TASK_PROCESS_SLICE = "processSlice"

    public static final String TASK_COMPILE_ICEDOC = "compileIcedoc"

    private Project project

    private IceExtension ice

    private final ProjectLayout layout

    @Inject
    IcePlugin(ProjectLayout layout) {
        this.layout = layout
    }

    @Override
    void apply(Project project) {
        this.project = project

        // Create ice extension
        this.ice = project.extensions.create(EXTENSION_ICE, IceExtension, project)

        // Apply the zeroc plugin
        project.pluginManager.apply(SlicePlugin)

        // Apply blitz plugin (which applies dsl and api plugins)
        project.pluginManager.apply(BlitzPlugin)

        // Use Api plugin to create ice combinedToIce task
        addCombinedToIce()

        // Register our tasks
        registerProcessSliceTask()
        registerIceDocsTask()
        registerZipIceDocs()

        // Sort out ordering of tasks
        configureTaskOrdering()
    }

    void addCombinedToIce() {
        ApiExtension api = project.extensions.getByType(ApiExtension)

        // Results in task named 'combinedToIce'
        api.language.create("ice", new Action<SplitExtension>() {
            @Override
            void execute(SplitExtension splitExtension) {
                splitExtension.setOutputDir("slice/omero/model")
                splitExtension.rename("\$1")
            }
        })
    }

    TaskProvider<Copy> registerProcessSliceTask() {
        project.tasks.register(TASK_PROCESS_SLICE, Copy, new Action<Copy>() {
            @Override
            void execute(Copy task) {
                // Copy ice files generated in combinedToIce task to iceSrcDir
                CopySpec combinedToIceSpec = project.copySpec()
                combinedToIceSpec.from(project.tasks.named("combinedToIce"))
                combinedToIceSpec.into("omero/model")

                // Copy files located in project dir to iceSrcDir
                CopySpec mainSpec = project.copySpec()
                mainSpec.from(layout.projectDirectory.dir("src/main/slice"))

                task.into(ice.iceSrcDir)
                task.with(mainSpec)
                task.with(combinedToIceSpec)
            }
        })
    }

    TaskProvider<IceDocsTask> registerIceDocsTask() {
        project.tasks.register(TASK_COMPILE_ICEDOC, IceDocsTask, new Action<IceDocsTask>() {
            @Override
            void execute(IceDocsTask t) {
                t.source = ice.iceSrcDir
                t.includeDirs.add(ice.iceSrcDir)
                t.outputDir.set(ice.docsOutputDir)
            }
        })
    }

    TaskProvider<Zip> registerZipIceDocs() {
        project.tasks.register(TASK_ZIP_ICEDOC, Zip, new Action<Zip>() {
            @Override
            void execute(Zip zip) {
                zip.archiveClassifier.set("icedoc")
                zip.from(project.tasks.named(TASK_COMPILE_ICEDOC))
            }
        })
    }

    void configureTaskOrdering() {
        def processSlice = project.tasks.named(TASK_PROCESS_SLICE)
        
        // Ice docs task depends on all ice files being present
        project.tasks.named(TASK_COMPILE_ICEDOC).configure {
            it.dependsOn(processSlice)
        }
    }

}
