package org.openmicroscopy.utils

import com.zeroc.gradle.icebuilder.slice.SliceExtension
import groovy.transform.CompileStatic
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.openmicroscopy.extensions.IceExtension

@CompileStatic
abstract class SliceHelper {

    static SliceHelper newInstance(Project project, IceExtension ice) {
        SliceExtension slice = project.extensions.getByType(SliceExtension)
        String iceVersion = slice.iceVersion as String
        SliceHelper sliceHelper = iceVersion.contains("3.7") ?
                new SliceHelper37(project, ice) : iceVersion.contains("3.6") ?
                new SliceHelper36(project, ice) : null
        if (sliceHelper == null) {
            throw new GradleException("Unknown ice version")
        }
        return sliceHelper
    }

    abstract void configure()

}
