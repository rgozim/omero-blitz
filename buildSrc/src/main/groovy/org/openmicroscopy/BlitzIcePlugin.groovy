package org.openmicroscopy

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Zip
import org.openmicroscopy.api.extensions.ApiExtension
import org.openmicroscopy.api.extensions.SplitExtension
import org.openmicroscopy.blitz.BlitzPlugin
import org.openmicroscopy.blitz.extensions.BlitzExtension
import org.openmicroscopy.dsl.tasks.FilesGeneratorTask
import org.openmicroscopy.dsl.tasks.GeneratorBaseTask
import org.openmicroscopy.extensions.IceExtension

@CompileStatic
class BlitzIcePlugin implements Plugin<Project> {

    public static final String TASK_ZIP_ICEDOC = "zipIcedoc"

    public static final String TASK_COPY_ICE_FILES = "copyIceFiles"

    private IceExtension ice

    private Project project

    @Override
    void apply(Project project) {
        this.project = project

        project.pluginManager.apply(IcePlugin)

        ice = project.extensions.getByType(IceExtension)

        // Ice tasks
        registerCopySliceFilesTask()
        registerZipIceDocs()

        configureClean()
        configureIceGen()
        configureTaskOrdering()
    }

    TaskProvider<Copy> registerCopySliceFilesTask() {
        project.tasks.register(TASK_COPY_ICE_FILES, Copy, new Action<Copy>() {
            @Override
            void execute(Copy task) {
                task.from(project.layout.projectDirectory.dir("src/main/slice"))
                task.into(ice.iceSrcDir)
            }
        })
    }

    TaskProvider<Zip> registerZipIceDocs() {
        project.tasks.register(TASK_ZIP_ICEDOC, Zip, new Action<Zip>() {
            @Override
            void execute(Zip zip) {
                zip.archiveClassifier.set("icedoc")
                zip.from(project.tasks.named(IcePlugin.TASK_COMPILE_ICEDOC))
            }
        })
    }

    void configureClean() {
        BlitzExtension blitz = project.extensions.getByType(BlitzExtension)

        project.tasks.named(BasePlugin.CLEAN_TASK_NAME, Delete).configure {
            it.delete blitz.outputDir
        }
    }

    void configureIceGen() {
        ApiExtension api = project.extensions.getByType(ApiExtension)

        api.language.create("ice", new Action<SplitExtension>() {
            @Override
            void execute(SplitExtension splitExtension) {
                splitExtension.setOutputDir("slice/omero/model")
                splitExtension.rename("\$1")
            }
        })
    }

    void configureTaskOrdering() {
        // Compile slice depends on all ice files being present
        project.tasks.named(IcePlugin.TASK_COMPILE_ICE).configure {
            it.dependsOn(project.tasks.named(TASK_COPY_ICE_FILES), project.tasks.named("combinedToIce"))
        }

        // Ice docs task depends on all ice files being present
        project.tasks.named(IcePlugin.TASK_COMPILE_ICEDOC).configure {
            it.dependsOn(project.tasks.named(TASK_COPY_ICE_FILES), project.tasks.named("combinedToIce"))
        }
    }

}
