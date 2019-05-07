package org.openmicroscopy

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import org.openmicroscopy.extensions.IceExtension
import org.openmicroscopy.tasks.IcePythonTask

@CompileStatic
class IcePythonPlugin implements Plugin<Project> {

    public static final String TASK_COMPILE_ICE_PYTHON = "compileIcePython"

    private Project project

    private IceExtension blitzIce

    @Override
    void apply(Project project) {
        this.project = project

        project.pluginManager.apply(IcePlugin)

        blitzIce = project.extensions.getByType(IceExtension)

        addPythonIceRegistrationRule()
        registerPythonTaskGroup()
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
                        task.dependsOn(project.tasks.named(BlitzIcePlugin.TASK_COPY_ICE_FILES))
                        task.includeDirs.from(blitzIce.iceSrcDir)
                        task.sourceFiles.from(project.fileTree(blitzIce.iceSrcDir.dir(dir)).matching {
                            include: "**.ice"
                        })
                        task.outputDir.set(blitzIce.pythonOutputDir)
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

    TaskProvider<Task> registerPythonTaskGroup() {
        project.tasks.register(TASK_COMPILE_ICE_PYTHON) {
            it.setGroup("slice")
            it.setDescription("Runs all ice python tasks")
            it.dependsOn("pythonIceOmero", "pythonIceOmeroModel",
                    "pythonIceOmeroCmd", "pythonIceOmeroApi")
        }
    }

}
