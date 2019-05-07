package org.openmicroscopy.extensions

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty

@CompileStatic
class IceExtension {

    final DirectoryProperty iceSrcDir

    final DirectoryProperty outputDir

    final DirectoryProperty docsOutputDir

    final DirectoryProperty pythonOutputDir

    IceExtension(Project project) {
        this.iceSrcDir = project.objects.directoryProperty()
        this.outputDir = project.objects.directoryProperty()
        this.docsOutputDir = project.objects.directoryProperty()
        this.pythonOutputDir = project.objects.directoryProperty()

        this.docsOutputDir.convention(project.layout.buildDirectory.dir("docs/icedoc"))
        this.pythonOutputDir.convention(project.layout.buildDirectory.dir("toArchive/python"))
    }
}
