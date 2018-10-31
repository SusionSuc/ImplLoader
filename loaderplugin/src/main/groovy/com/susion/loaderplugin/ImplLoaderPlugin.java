package com.susion.loaderplugin;

import com.android.build.gradle.BaseExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Created by susion on 2018/10/29.
 */

public class ImplLoaderPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {

        project.getExtensions().findByType(BaseExtension.class)
                .registerTransform(new ImplLoaderTransform());
    }
}
