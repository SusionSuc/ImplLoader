package com.susion.compiler2;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.susion.annotation2.ProtocolConstants;
import com.susion.annotation2.RegisterImplLoaderInfo;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by susion on 2018/10/27.
 * 生成 ImplInfo_xxxx.java
 */
public class ImplClassProtocolGenerate {

    private Elements elementsUtils;
    private Filer filer;

    ImplClassProtocolGenerate(Elements elements, Filer filer) {
        elementsUtils = elements;
        this.filer = filer;
    }

    void generateImplProtocolClass(HashMap<String, ImplAnnotationInfo> implMap) {

        if (implMap == null || implMap.size() == 0) return;

        TypeSpec.Builder implProtocolSpec = getImplProtocolSpec();
        MethodSpec.Builder implProtocolMethodSpec = getImplProtocolMethodSpec();

        for (String implName : implMap.keySet()) {
            CodeBlock registerBlock = getImplProtocolInitCode(implMap.get(implName));
            implProtocolMethodSpec.addCode(registerBlock);
        }
        implProtocolSpec.addMethod(implProtocolMethodSpec.build());
        writeImplProtocolCode(implProtocolSpec.build());
    }

    private void writeImplProtocolCode(TypeSpec code) {
        try {
            //每次编译都会生成一个文件。  FIXME:上次编译的文件应该删除
            JavaFile.builder(ProtocolConstants.IMPL_PROTOCOL_GEN_PKG, code)
                    .build()
                    .writeTo(filer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private CodeBlock getImplProtocolInitCode(ImplAnnotationInfo implAnnotationInfo) {
        CodeBlock.Builder codeBuild = CodeBlock.builder();
        ClassName implLoader = className(ProtocolConstants.IMPL_LOADER_CLASS);
        if (implLoader == null) return codeBuild.build();
        codeBuild.addStatement("$T." + ProtocolConstants.IMPL_LOADER_REGISTER_IMPL_METHOD + "($S, $T.class)", implLoader, implAnnotationInfo.name, implAnnotationInfo.implClass);
        return codeBuild.build();
    }

    private MethodSpec.Builder getImplProtocolMethodSpec() {
        return MethodSpec.methodBuilder(ProtocolConstants.IMPL_LOADER_HELP_INIT_METHOD).addModifiers(Modifier.PUBLIC, Modifier.STATIC).returns(TypeName.VOID);
    }

    private TypeSpec.Builder getImplProtocolSpec() {
        return TypeSpec.classBuilder(ProtocolConstants.IMPL_INFO_CLASS_PREFIX + "_" + UUID.randomUUID().toString().replace('-', '_'))
                .addSuperinterface(ClassName.get(RegisterImplLoaderInfo.class))
                .addModifiers(Modifier.PUBLIC);
    }

    /**
     * 从字符串获取ClassName对象
     */
    public ClassName className(String className) {
        TypeElement element = typeElement(className);
        if (element == null) return null;
        return ClassName.get(element);
    }

    /**
     * 从字符串获取TypeElement对象
     */
    public TypeElement typeElement(String className) {
        return elementsUtils.getTypeElement(className);
    }


}
