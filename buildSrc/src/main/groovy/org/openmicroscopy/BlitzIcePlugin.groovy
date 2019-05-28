package org.openmicroscopy

import com.zeroc.gradle.icebuilder.slice.SlicePlugin
import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.openmicroscopy.blitz.BlitzPlugin
import org.openmicroscopy.extensions.IceExtension

@CompileStatic
class BlitzIcePlugin implements Plugin<Project> {

    public static final String EXTENSION_ICE = "ice"

    public static final String TASK_ZIP_ICEDOC = "zipIcedoc"

    public static final String TASK_PROCESS_SLICE = "processSlice"

    public static final String TASK_COMPILE_ICE = "compileSlice"

    public static final String TASK_COMPILE_ICEDOC = "compileIcedoc"

    private IceExtension ice

    private Project project

    @Override
    void apply(Project project) {
        this.project = project

        project.pluginManager.apply(BlitzPlugin)
        project.pluginManager.apply(SlicePlugin)

        // Create ice extension
        ice = project.extensions.create(EXTENSION_ICE, IceExtension, project)


        // Ice tasks
//        registerCopySliceFilesTask()
//        registerIceDocsTask()
//        registerZipIceDocs()
//        configureCompileSlice()
//        registerCombinedToIce()
//
//        configureTaskOrdering()
    }

//    TaskProvider<Copy> registerCopySliceFilesTask() {
//        project.tasks.register(TASK_PROCESS_SLICE, Copy, new Action<Copy>() {
//            @Override
//            void execute(Copy task) {
//                task.from(project.layout.projectDirectory.dir("src/main/slice"))
//                task.into(ice.iceSrcDir)
//            }
//        })
//    }
//
//    TaskProvider<IceDocsTask> registerIceDocsTask(IceExtension ice) {
//        project.tasks.register(TASK_COMPILE_ICEDOC, IceDocsTask, new Action<IceDocsTask>() {
//            @Override
//            void execute(IceDocsTask t) {
//                t.source = ice.iceSrcDir
//                t.includeDirs.add(ice.iceSrcDir)
//                t.outputDir.set(ice.docsOutputDir)
//            }
//        })
//    }
//
//    TaskProvider<Zip> registerZipIceDocs() {
//        project.tasks.register(TASK_ZIP_ICEDOC, Zip, new Action<Zip>() {
//            @Override
//            void execute(Zip zip) {
//                zip.archiveClassifier.set("icedoc")
//                zip.from(project.tasks.named(TASK_COMPILE_ICEDOC))
//            }
//        })
//    }
//
//    void configureCompileSlice(IceExtension ice) {
//        SliceExtension slice = project.extensions.getByType(SliceExtension)
//
//        slice.java.create("main", new Action<Java>() {
//            @Override
//            void execute(Java java) {
//                java.include = [project.file(ice.iceSrcDir)]
//                java.files = project.fileTree(ice.iceSrcDir).matching {
//                    include: "**/*.ice"
//                }
//                java.args = "--tie"
//            }
//        })
//    }
//
//    void registerCombinedToIce() {
//        ApiExtension api = project.extensions.getByType(ApiExtension)
//
//        api.language.create("ice", new Action<SplitExtension>() {
//            @Override
//            void execute(SplitExtension splitExtension) {
//                splitExtension.setOutputDir("slice/omero/model")
//                splitExtension.rename("\$1")
//            }
//        })
//    }
//
//    void configureTaskOrdering() {
//        // Compile slice depends on all ice files being present
//        project.tasks.named(IcePlugin.TASK_COMPILE_ICE).configure {
//            it.dependsOn(project.tasks.named(TASK_PROCESS_SLICE), project.tasks.named("combinedToIce"))
//        }
//
//        // Ice docs task depends on all ice files being present
//        project.tasks.named(IcePlugin.TASK_COMPILE_ICEDOC).configure {
//            it.dependsOn(project.tasks.named(TASK_PROCESS_SLICE), project.tasks.named("combinedToIce"))
//        }
//    }

}
