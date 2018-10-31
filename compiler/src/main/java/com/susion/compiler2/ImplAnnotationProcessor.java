package com.susion.compiler2;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.susion.annotation2.Impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;


/**
 * Created by susion on 2018/10/26.
 * 处理 @Impl
 */
@AutoService(Processor.class)
public class ImplAnnotationProcessor extends AbstractProcessor {

    private Messager annotationLog;
    private Elements elementsUitls;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        annotationLog = processingEnv.getMessager();
        elementsUitls = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (annotations == null || annotations.isEmpty()) {
            return false;
        }

        informMsg("注解处理器运行.......................................");

        HashMap<String, ImplAnnotationInfo> implMap = new HashMap<>();

        for (Element implElement : roundEnv.getElementsAnnotatedWith(Impl.class)) {

            if (implElement.getKind() != ElementKind.CLASS) {
                informError(implElement, "@Impl 只能注解在类上！注解失效！");
                continue;
            }

            ImplAnnotationInfo implAnnotationInfo = getImplAnnotationInfo((TypeElement) implElement);

            if (implAnnotationInfo == null) continue;

            implMap.put(implAnnotationInfo.name, implAnnotationInfo);
        }

        new ImplClassProtocolGenerate(elementsUitls, filer).generateImplProtocolClass(implMap);

        informMsg("注解处理器运行结束.......................................");

        return true;
    }

    //implClassElement 为被注解的类
    private ImplAnnotationInfo getImplAnnotationInfo(TypeElement implClassElement) {
        Impl implAnnotation = implClassElement.getAnnotation(Impl.class);
        ClassName implClassName = ClassName.get(implClassElement);
        String implName = implAnnotation.name();
        return new ImplAnnotationInfo(implName, implClassName);
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> set = new HashSet<>();
        set.add(Impl.class.getCanonicalName());
        return set;
    }


    private void informError(Element e, String msg, Object... args) {
        annotationLog.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args),
                e);
    }

    private void informMsg(String msg, Object... args) {
        annotationLog.printMessage(
                Diagnostic.Kind.NOTE,
                String.format(msg, args));
    }

}
