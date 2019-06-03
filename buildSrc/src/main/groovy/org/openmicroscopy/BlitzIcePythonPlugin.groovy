package org.openmicroscopy

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.bundling.Zip
import org.openmicroscopy.api.extensions.ApiExtension
import org.openmicroscopy.api.extensions.SplitExtension
import org.openmicroscopy.blitz.BlitzPlugin
import org.openmicroscopy.blitz.extensions.BlitzExtension
import org.openmicroscopy.dsl.DslPluginBase
import org.openmicroscopy.dsl.extensions.DslExtension
import org.openmicroscopy.dsl.extensions.SingleFileConfig
import org.openmicroscopy.extensions.IceExtension

@CompileStatic
class BlitzIcePythonPlugin implements Plugin<Project> {

    public static final String TASK_ZIP_PYTHON = "zipPython"

    private Project project

    private IceExtension ice

    @Override
    void apply(Project project) {
        this.project = project

        project.pluginManager.apply(BlitzPlugin)
        project.pluginManager.apply(IcePythonPlugin)

        // Set ice conventions based on BlitzExtension
        ice = project.extensions.getByType(IceExtension)

        addPythonConfigurations()
        addZipPythonTask()
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
                    dir.asFile }
                )
                splitExtension.rename("omero_model_\$1I")
            }
        })

        project.tasks.named(IcePythonPlugin.TASK_COMPILE_ICE_PYTHON).configure {
            String objectFactoryRegistrarName = dsl.createTaskName("objectFactoryRegistrar")
            it.dependsOn(
                    project.tasks.named("combinedToPython"),
                    project.tasks.named(objectFactoryRegistrarName)
            )
        }
    }

    TaskProvider<Zip> addZipPythonTask() {
        project.tasks.register(TASK_ZIP_PYTHON, Zip, new Action<Zip>() {
            @Override
            void execute(Zip zip) {
                zip.dependsOn(IcePythonPlugin.TASK_COMPILE_ICE_PYTHON)
                zip.archiveClassifier.set("python")
                zip.from ice.pythonOutputDir
            }
        })
    }

}
