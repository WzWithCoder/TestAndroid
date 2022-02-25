package com.example;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.util.ArrayList;


//http://www.jianshu.com/p/c202853059b4
//http://blog.csdn.net/sgwhp/article/details/50239747
public class TestAgent {

    //VirtualMachine.loadAgent(agent.jar)
    public static void agentmain(String args, Instrumentation instrumentation) {
        MainTransformer transformer = new MainTransformer();
        instrumentation.addTransformer(transformer, true);

        retransformClasses(instrumentation, transformer);
    }

    //-javaagent agent.jar
    public static void premain(String args, Instrumentation instrumentation) {
        System.out.println("---------------------------premain---------------------------" + args);
        agentmain(args, instrumentation);
    }

    public static void retransformClasses(Instrumentation instrumentation, ClassFileTransformer transformer) {
        Class[] classes = instrumentation.getAllLoadedClasses();
        ArrayList<Class> classesToBeTransform = new ArrayList<>();
        for (Class clazz : classes) {
            if (MainTransformer.transforms(clazz)) {
                classesToBeTransform.add(clazz);
            }
        }
        try {
            if (!classesToBeTransform.isEmpty() && instrumentation.isRetransformClassesSupported()) {

                Class[] classArray = classesToBeTransform
                        .toArray(new Class[classesToBeTransform.size()]);
                instrumentation.retransformClasses(classArray);
            }
//            redefineClass(instrumentation, transformer, ProcessBuilder.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void redefineClass(Instrumentation instrumentation, ClassFileTransformer transformer, Class<?> clazz)
            throws IOException, IllegalClassFormatException, ClassNotFoundException, UnmodifiableClassException {
        String internalName = clazz.getName().replace('.', '/');
        String fullName = internalName + ".class";
        ClassLoader classLoader = clazz.getClassLoader() == null ?
                TestAgent.class.getClassLoader() : clazz.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fullName);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Utils.copy(inputStream, outputStream);
        inputStream.close();

        byte[] arrayOfByte = transformer.transform(clazz.getClassLoader(), internalName, clazz
                , null, outputStream.toByteArray());
        ClassDefinition classDefinition = new ClassDefinition(clazz, arrayOfByte);
        instrumentation.redefineClasses(classDefinition);
    }
}
