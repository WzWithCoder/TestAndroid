package com.annotation.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.wz.arouter.annotation.Route;

import org.apache.commons.collections4.MapUtils;

import java.io.IOException;
import java.util.HashMap;
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
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"com.wz.arouter.annotation.Route"})
public class RouteProcessor extends AbstractProcessor {
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
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Route.class);
        Map<String, InjectEntity> routeMap = new HashMap<>();
        for (Element element : elements) {
            if (!(element instanceof TypeElement)) {
                continue;
            }

            TypeElement typeElement = (TypeElement) element;
            String qualifiedName = typeElement.getQualifiedName().toString();

            Route annotation = typeElement.getAnnotation(Route.class);
            String key = annotation.value();

            InjectEntity injectEntity = routeMap.get(key);
            if (injectEntity == null) {
                String className = typeElement.getSimpleName().toString();
                PackageElement packageElement = mElementUtil.getPackageOf(typeElement);
                String packageName = packageElement.getQualifiedName().toString();

                injectEntity = new InjectEntity(packageName, className, qualifiedName);
                routeMap.put(key, injectEntity);
            }
        }
        createInjectFile(routeMap);
    }

    //https://github.com/square/javapoet
    //https://github.com/javaparser
    private void createInjectFile(Map<String, InjectEntity> routeMap) {
        if (routeMap == null || routeMap.isEmpty()) return;
        mLogger.printMessage(Diagnostic.Kind.NOTE, "RouteInjector--" + routeMap.toString());

        MethodSpec.Builder inflateBuilder = MethodSpec.methodBuilder("inflate")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ParameterizedTypeName.get(Map.class, String.class, String.class), "map")
                .returns(void.class);
        for (Map.Entry<String, InjectEntity> entry : routeMap.entrySet()) {
            inflateBuilder.addStatement("map.put( $S,$S)", entry.getKey(), entry.getValue().qualifiedName);
        }
        MethodSpec inflate = inflateBuilder.build();

        TypeSpec typeSpec = TypeSpec.classBuilder("RouteInjector_Inner")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(inflate)
                .build();

        try {
            JavaFile javaFile = JavaFile.builder("com.wz.arouter", typeSpec)
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

        public InjectEntity(String packageName, String className, String qualifiedName) {
            this.packageName = packageName;
            this.className = className;
            this.qualifiedName = qualifiedName;
        }

        @Override
        public String toString() {
            return "InjectEntity{" +
                    "packageName='" + packageName + '\'' +
                    ", className='" + className + '\'' +
                    ", qualifiedName='" + qualifiedName +
                    '}';
        }
    }
}
