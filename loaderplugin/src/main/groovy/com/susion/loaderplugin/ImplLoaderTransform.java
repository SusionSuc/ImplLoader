package com.susion.loaderplugin;

import com.android.build.api.transform.Format;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.google.common.collect.ImmutableSet;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by susion on 2018/10/29.
 */

public class ImplLoaderTransform extends Transform {


    @Override
    public String getName() {
        return "impl_loader_transform";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

//    @Override
//    public Set<? super QualifiedContent.Scope> getReferencedScopes() {
//        return TransformManager.SCOPE_FULL_PROJECT;
//    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {

        Set<String> implInfoClasses = new HashSet<>();

        System.out.println("开始 transform.............. ");

        for (TransformInput input : transformInvocation.getInputs()) {
            input.getJarInputs().forEach(jarInput -> {
                try {
                    File jarFile = jarInput.getFile();
                    File dst = transformInvocation.getOutputProvider().getContentLocation(
                            jarInput.getName(), jarInput.getContentTypes(), jarInput.getScopes(),
                            Format.JAR);
                    implInfoClasses.addAll(InsertImplInfoCode.getImplInfoClassesFromJar(jarFile));
                    FileUtils.copyFile(jarFile, dst);   //必须要把输入，copy到输出，不然接下来没有办法处理
                } catch (IOException e) {
                }
            });

            input.getDirectoryInputs().forEach(directoryInput -> {

                try {
                    File dir = directoryInput.getFile();
                    File dst = transformInvocation.getOutputProvider().getContentLocation(
                            directoryInput.getName(), directoryInput.getContentTypes(),
                            directoryInput.getScopes(), Format.DIRECTORY);
                    implInfoClasses.addAll(InsertImplInfoCode.getImplInfoClassesFromDir(dir));
                    FileUtils.copyDirectory(dir, dst); //必须要把输入，copy到输出，不然接下来没有办法处理
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        System.out.println("扫描的 ImplInfo类的个数为: " + implInfoClasses.size());

        File dest = transformInvocation.getOutputProvider().getContentLocation(
                "ImplLoader", TransformManager.CONTENT_CLASS,
                ImmutableSet.of(QualifiedContent.Scope.PROJECT), Format.DIRECTORY);

        InsertImplInfoCode.insertImplInfoInitMethod(implInfoClasses, dest.getAbsolutePath());

        System.out.println("结束 transform ................ ");
    }


}
