package com.wangyu.apt;

import com.squareup.javapoet.JavaFile;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * author by wangyu
 * Use For
 * created on 2018/4/2.
 */

public abstract class ProcessorImpl<T extends ProcessorImpl.AnnotatedClass> extends AbstractProcessor {
    private Filer mFiler; //文件相关的辅助类
    private Elements mElementUtils; //元素相关的辅助类  许多元素
    private Messager mMessager; //日志相关的辅助类

    private Map<String, T> mAnnotatedClassMap;
    private Map<TypeElement, Map<Class<? extends Annotation>, List<Element>>> mAnnotation;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
        mMessager = processingEnv.getMessager();
        mAnnotatedClassMap = new TreeMap<>();
        mAnnotation = new HashMap<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mAnnotatedClassMap.clear();
        mAnnotation.clear();
        log("process");
        try {
            processActivityCheck(roundEnv);
        } catch (Exception e) {
            e.printStackTrace();
            error(e.getMessage());
        }
        for (TypeElement typeElement : mAnnotation.keySet()) {
            log("file name " + typeElement.getSimpleName().toString());
            for (Class elements : mAnnotation.get(typeElement).keySet()) {
                log("Annotation " + elements.getName());
                for (Element element :
                        mAnnotation.get(typeElement).get(elements)) {
                    log("element " + element.getSimpleName().toString());
                }
            }
        }
        for (T annotatedClass : mAnnotatedClassMap.values()) {
            try {
                annotatedClass.javaFile.writeTo(mFiler);
            } catch (Exception e) {
//                e.printStackTrace();
                error("Generate file failed, reason: %s", e.getMessage());
            }
        }
        return true;
    }

    private void processActivityCheck(RoundEnvironment roundEnv) throws IllegalArgumentException, ClassNotFoundException {
        //check ruleslass forName(String className
        for (Class<? extends Annotation> annotation : provideAnnotation()) {
//            log("annotation " + annotation.getName());
            for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
//                log("element " + element.toString());
                getAnnotatedClass(annotation, element);
            }
        }
        provideFile();
    }

    protected void provideFile() {
        for (TypeElement fullName : mAnnotation.keySet()) {
            mAnnotatedClassMap.computeIfAbsent(fullName.getQualifiedName().toString(), k -> provideAnnotatedClass(fullName, mElementUtils, mAnnotation.get(fullName)));
        }
    }

    protected abstract T provideAnnotatedClass(TypeElement typeElement, Elements elements, Map<Class<? extends Annotation>, List<Element>> classListMap);

    private void getAnnotatedClass(Class<? extends Annotation> annotation, final Element element) {
        // tipe . can not use chines  so  ....
        // get TypeElement  element is class's --->class  TypeElement typeElement = (TypeElement) element
        //  get TypeElement  element is method's ---> TypeElement typeElement = (TypeElement) element.getEnclosingElement();
        TypeElement typeElement = (TypeElement) element.getEnclosingElement();
//        String fullName = typeElement.getQualifiedName().toString();
        Map<Class<? extends Annotation>, List<Element>> classListMap = mAnnotation.get(typeElement);
        if (classListMap == null) {
            classListMap = new HashMap<>();
            ArrayList<Element> elements=new ArrayList<>();
            elements.add(element);
            classListMap.put(annotation,elements);
            mAnnotation.put(typeElement, classListMap);
        } else {
            List<Element> elements = classListMap.get(annotation);
            if (elements == null) {
                elements = new ArrayList<>();
                elements.add(element);
                classListMap.put(annotation,elements);
                mAnnotation.put(typeElement, classListMap);
            } else
                mAnnotation.get(typeElement).get(annotation).add(element);
        }

//        log(annotationList.size() + "fdas");
    }


    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    abstract List<Class<? extends Annotation>> provideAnnotation();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : provideAnnotation()) {
            types.add(annotation.getName());
        }
        return types;
    }

    private void error(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args));
    }

    protected void log(String msg, Object... args) {
        mMessager.printMessage(Diagnostic.Kind.WARNING, String.format(msg, args));
    }

    abstract static class AnnotatedClass {
        //        TypeElement mTypeElement;//activity  //fragmemt
//        Elements mElements;
//        Element mField;
        JavaFile javaFile;

        AnnotatedClass(TypeElement typeElement, Elements elements, Map<Class<? extends Annotation>, List<Element>> mField) {
//            mTypeElement = typeElement;
//            mElements = elements;
//            this.mField = mField;
            javaFile = generateActivityFile(typeElement, elements, mField);
        }

        abstract JavaFile generateActivityFile(TypeElement mTypeElement, Elements mElements, Map<Class<? extends Annotation>, List<Element>> mField);
    }

}
