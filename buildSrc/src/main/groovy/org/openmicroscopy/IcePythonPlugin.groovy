package org.openmicroscopy

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Zip
import org.openmicroscopy.api.extensions.ApiExtension
import org.openmicroscopy.api.extensions.SplitExtension
import org.openmicroscopy.dsl.extensions.DslExtension
import org.openmicroscopy.dsl.extensions.SingleFileConfig
import org.openmicroscopy.extensions.IceExtension
import org.openmicroscopy.tasks.IcePythonTask

@CompileStatic
class IcePythonPlugin implements Plugin<Project> {

    public static final String TASK_ZIP_PYTHON = "zipPython"

    public static final String TASK_COMPILE_ICE_PYTHON = "compileIcePython"

    private Project project

    private IceExtension ice

    @Override
    void apply(Project project) {
        project.pluginManager.apply(IcePlugin)

        this.project = project
        this.ice = project.extensions.getByType(IceExtension)

        addPythonIceRegistrationRule()
        addPythonTaskGroup()
        addPythonConfigurations()
        addZipPythonTask()
    }

    void addPythonIceRegistrationRule() {
        project.tasks.addRule("Pattern: pythonIce<ID>", new Action<String>() {
            @Override
            void execute(String taskName) {
                String id = taskName.replace("pythonIce", "")
                String camel = id.substring(0, 1).toLowerCase() + id.substring(1)
                String dir = camel.replaceAll("([A-Z])", '/$1').toLowerCase()
                String dirAsPrefix = dir.replace("/", "_") + "_"
                project.tasks.register(taskName, IcePythonTask, new Action<IcePythonTask>() {
                    @Override
                    void execute(IcePythonTask task) {
                        task.dependsOn(project.tasks.named(IcePlugin.TASK_PROCESS_SLICE))
                        task.source = project.fileTree(ice.iceSrcDir.dir(dir)).matching {
                            include: "**.ice"
                        }
                        task.includeDirs.add(ice.iceSrcDir)
                        task.outputDir.set(ice.pythonOutputDir)
                        task.prefix.set(dirAsPrefix)
                    }
                })
            }
        })

        // Register rule based tasks
        project.tasks.register("pythonIceOmero")
        project.tasks.register("pythonIceOmeroModel")
        project.tasks.register("pythonIceOmeroCmd")
        project.tasks.register("pythonIceOmeroApi")
    }

    TaskProvider<Task> addPythonTaskGroup() {
        project.tasks.register(TASK_COMPILE_ICE_PYTHON) {
            it.setGroup("slice")
            it.setDescription("Runs all ice python tasks")
            it.dependsOn("pythonIceOmero", "pythonIceOmeroModel",
                    "pythonIceOmeroCmd", "pythonIceOmeroApi")
        }
    }

    void addPythonConfigurations() {
        DslExtension dsl = project.extensions.getByType(DslExtension)
        dsl.singleFile.create("objectFactoryRegistrar", new Action<SingleFileConfig>() {
            @Override
            void execute(SingleFileConfig singleFileConfig) {
                singleFileConfig.setTemplate("py_obj_reg.vm")
                singleFileConfig.setOutputFile(ice.pythonOutputDir.map { Directory dir ->
                    new File(dir.asFile, "omero/ObjectFactoryRegistrar.py")
                })
            }
        })

        ApiExtension api = project.extensions.getByType(ApiExtension)
        api.language.create("python", new Action<SplitExtension>() {
            @Override
            void execute(SplitExtension splitExtension) {
                splitExtension.setOutputDir(ice.pythonOutputDir.map { Directory dir ->
                    dir.asFile
                })
                splitExtension.rename("omero_model_\$1I")
            }
        })

        project.tasks.named(TASK_COMPILE_ICE_PYTHON).configure {
            Provider<String> objectFactoryRegistrarName =
                    dsl.createTaskName("objectFactoryRegistrar")
            it.dependsOn(project.tasks.named("combinedToPython"),
                    project.tasks.named(objectFactoryRegistrarName.get()))
        }
    }

    TaskProvider<Zip> addZipPythonTask() {
        project.tasks.register(TASK_ZIP_PYTHON, Zip, new Action<Zip>() {
            @Override
            void execute(Zip zip) {
                zip.dependsOn(TASK_COMPILE_ICE_PYTHON)
                zip.archiveClassifier.set("python")
                zip.from ice.pythonOutputDir
            }
        })
    }

}
