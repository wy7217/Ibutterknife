package com.wangyu.apt;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.wangyu.annotation.OnClick;
import com.wangyu.annotation.ViewInject;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;


@AutoService(Processor.class)
public class ViewInjectProcessor extends ProcessorImpl<ViewInjectProcessor.ViewInjectClass> {

    class ViewInjectClass extends ProcessorImpl.AnnotatedClass {
        ViewInjectClass(TypeElement typeElement, Elements elements, Map<Class<? extends Annotation>, List<Element>> mField) {
            super(typeElement, elements, mField);
        }


        @Override
        JavaFile generateActivityFile(TypeElement mTypeElement, Elements mElements, Map<Class<? extends Annotation>, List<Element>> mField) {
            MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                    .addParameter(TypeName.get(mTypeElement.asType()), "activity", Modifier.FINAL);
//            log(mField.size()+"ffff");
            getMethodString(mField, constructor);
            // build inject method
            //generaClass
            TypeSpec injectClass = TypeSpec.classBuilder(mTypeElement.getSimpleName() + "_Inject")
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(constructor.build())
                    .build();
            String packageName = mElements.getPackageOf(mTypeElement).getQualifiedName().toString();
            return JavaFile.builder(packageName, injectClass).build();
        }
    }

    @Override
    protected ViewInjectClass provideAnnotatedClass(TypeElement typeElement, Elements elements, Map<Class<? extends Annotation>, List<Element>> mField) {
        return new ViewInjectClass(typeElement, elements, mField);
    }

    private void getViewInjectMethodString(List<Element> elements, MethodSpec.Builder constructor) {
        for (Element element : elements) {
            ViewInject inject = element.getAnnotation(ViewInject.class);
            StringBuilder stringBuilder = new StringBuilder();
            String className = TypeName.get(element.asType()).toString();
            stringBuilder.append("activity.").append(element.getSimpleName()).append("=").append("(").append(className).append(")").
                    append("activity.findViewById(").append(inject.value()).append("").append(")");
            constructor.addStatement(stringBuilder.toString());
        }
    }

    private void getOnClickMethodString(List<Element> elements, MethodSpec.Builder constructor) {
        for (Element element : elements) {
            OnClick onclick = element.getAnnotation(OnClick.class);
            for (int id : onclick.value()) {
                String builder = "activity.findViewById(" + id + ")" + ".setOnClickListener(new android.view.View.OnClickListener() {\n" +
                        "                    @Override\n" +
                        "                    public void onClick(android.view.View v) {" +
                        "activity." + element.getSimpleName().toString() + "(v);" + "  }\n" +
                        "                })";
                constructor.addStatement(builder);
            }
        }
    }

    private void getMethodString(Map<Class<? extends Annotation>, List<Element>> mField, MethodSpec.Builder constructor) {
        for (Map.Entry<Class<? extends Annotation>, List<Element>> entry : mField.entrySet()) {
            if (entry.getKey().equals(ViewInject.class)) {
                getViewInjectMethodString(entry.getValue(), constructor);
            } else if (entry.getKey().equals(OnClick.class)) {
                getOnClickMethodString(entry.getValue(), constructor);
            }
        }

    }

    @Override
    List<Class<? extends Annotation>> provideAnnotation() {
        List<Class<? extends Annotation>> classes = new ArrayList<>();
        classes.add(ViewInject.class);
        classes.add(OnClick.class);
        return classes;
    }
}