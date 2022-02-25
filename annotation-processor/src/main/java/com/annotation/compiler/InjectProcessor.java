package com.annotation.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.wz.arouter.BindViews;
import com.wz.arouter.annotation.InjectView;
import com.wz.arouter.annotation.Route;

import org.apache.commons.collections4.MapUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

//https://developer.android.google.cn/studio/releases/gradle-plugin
//https://www.toutiao.com/i6810212310238364164/
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"com.wz.arouter.annotation.InjectView"})
public class InjectProcessor extends AbstractProcessor {
    private Filer    mFiler;       // File util, write class file into disk.
    private Messager mLogger;
    private Elements mElementUtil;


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();                  // Generate class.
        mElementUtil = processingEnv.getElementUtils();      // Get class meta.
        mLogger = processingEnv.getMessager();   // Package the log utils.
        Map<String, String> options = processingEnv.getOptions();
        if (MapUtils.isNotEmpty(options)) {
            String moduleName = options.get("moduleName");
            mLogger.printMessage(Diagnostic.Kind.NOTE, "moduleName--" + moduleName);
        }
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        TreeMaker treeMaker = TreeMaker.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations != null) {
            generater(roundEnv);
            return true;
        }
        return false;
    }

    //http://blog.csdn.net/wzgiceman/article/details/54580745
    private void generater(RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(InjectView.class);
        Map<String, InjectEntity> bindMap = new HashMap<>();
        for (Element element : elements) {
            if (!(element instanceof VariableElement)) {
                continue;
            }

            VariableElement variableElement = (VariableElement) element;
            TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
            String qualifiedName = typeElement.getQualifiedName().toString();

            InjectEntity injectEntity = bindMap.get(qualifiedName);
            if (injectEntity == null) {
                String className = typeElement.getSimpleName().toString();
                PackageElement packageElement = mElementUtil.getPackageOf(typeElement);
                String packageName = packageElement.getQualifiedName().toString();

                injectEntity = new InjectEntity(packageName, className, qualifiedName);
                bindMap.put(qualifiedName, injectEntity);
            }

            InjectView annotation = variableElement.getAnnotation(InjectView.class);
            int id = annotation.value();
            String viewName = variableElement.getSimpleName().toString();
            String viewType = variableElement.asType().toString();

            injectEntity.addView(new ViewEntity(id, viewType, viewName));
        }
        for (InjectEntity entity : bindMap.values()) {
            createInjectFile(entity);
        }
    }

    //https://github.com/square/javapoet
    //https://github.com/javaparser
    private void createInjectFile(InjectEntity data) {
        if (data == null || data.views.isEmpty()) return;
        mLogger.printMessage(Diagnostic.Kind.NOTE, "InjectEntity--" + data.toString());
        ClassName targetType = ClassName.bestGuess(data.qualifiedName);

        MethodSpec.Builder injectBuilder = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(Object.class, "source")
                .addParameter(targetType, "target")
                .returns(void.class);
        for (ViewEntity view : data.views) {
            injectBuilder.addStatement(
                    "target.$N = ($T)findViewById(source,$L)",
                    view.name, ClassName.bestGuess(view.type), view.id);
        }
        MethodSpec inject = injectBuilder.build();

        MethodSpec findViewById = MethodSpec.methodBuilder("findViewById")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(Object.class)
                .addParameter(Object.class, "source")
                .addParameter(Integer.class, "id")
                .addCode("if(source instanceof android.app.Activity){\n"
                        + "  return ((android.app.Activity)source).findViewById(id);\n"
                        + "} else if(source instanceof android.app.Dialog){\n"
                        + "  return ((android.app.Dialog)source).findViewById(id);\n"
                        + "} else if(source instanceof android.view.View){\n"
                        + "  return ((android.view.View)source).findViewById(id);\n"
                        + "} else {\n"
                        + "  return null;\n"
                        + "}\n")
                .build();

        ClassName interfaceName = ClassName.bestGuess("com.wz.arouter.ViewInjector");
        TypeSpec typeSpec = TypeSpec.classBuilder(data.className + BindViews.SEPARATOR + "ViewInjector")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(inject)
                .addMethod(findViewById)
                .addSuperinterface(ParameterizedTypeName.get(interfaceName, targetType))
                .build();

        try {
            JavaFile javaFile = JavaFile.builder(data.packageName, typeSpec)
                    .addFileComment("Generated code from Processor. Do not modify!")
                    .build();
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private class InjectEntity {
        public String packageName;
        public String className;
        public String qualifiedName;

        public List<ViewEntity> views;

        public InjectEntity(String packageName, String className, String qualifiedName) {
            this.packageName = packageName;
            this.className = className;
            this.qualifiedName = qualifiedName;
        }

        public void addView(ViewEntity data) {
            if (views == null) {
                views = new ArrayList<>();
            }
            views.add(data);
        }

        @Override
        public String toString() {
            return "InjectEntity{" +
                    "packageName='" + packageName + '\'' +
                    ", className='" + className + '\'' +
                    ", qualifiedName='" + qualifiedName + '\'' +
                    ", views=" + views +
                    '}';
        }
    }

    private class ViewEntity {
        public int    id;
        public String type;
        public String name;

        public ViewEntity(int id, String type, String name) {
            this.id = id;
            this.type = type;
            this.name = name;
        }

        @Override
        public String toString() {
            return "ViewEntity{" +
                    "id=" + id +
                    ", type='" + type + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    private boolean verify(Element element) {
        Route route = element.getAnnotation(Route.class);
        // It must be implement the interface IInterceptor and marked with annotation Interceptor.
        return null != route && ((TypeElement) element).getInterfaces().contains(route);
    }
}
