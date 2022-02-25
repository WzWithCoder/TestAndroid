package com.wz.gradle

import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * Created by wangzheng on 2017/2/17.
 */
class InjectClass {
    private Project project
    private ExpConfig config

    InjectClass(Project project) {
        this.project = project
        config = project.exceptionConfig
    }

    void injectDir(String path) {
        File dir = new File(path)
        if (dir.isDirectory()) {
            dir.eachFileRecurse { File file ->
                def classPath = file.absolutePath
                if (shouldProcess(classPath)) {
                    injectClass(file)
                }
            }
        }
    }

    void injectJar(String path) throws Exception {
        File jarFile = new File(path)
        def optJar = new File(jarFile.getParent(), jarFile.name + ".opt")
        if (optJar.exists()) {
            optJar.delete()
        }

        JarOutputStream jarOutputStream =
                new JarOutputStream(new FileOutputStream(optJar))

        def file = new JarFile(jarFile)
        Enumeration enumeration = file.entries()

        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement()
            InputStream inputStream = file.getInputStream(jarEntry)

            String entryName = jarEntry.getName()
            ZipEntry zipEntry = new ZipEntry(entryName)
            jarOutputStream.putNextEntry(zipEntry)
            def bytes = null;
            if (shouldProcess(entryName)) {
                bytes = injectClass(inputStream)
            }
            if (bytes == null) {
                bytes = IOUtils.toByteArray(inputStream)
            }
            jarOutputStream.write(bytes)
            inputStream.close()
            jarOutputStream.closeEntry()
        }
        jarOutputStream.close()
        file.close()

        if (jarFile.exists()) {
            jarFile.delete()
        }
        optJar.renameTo(jarFile)
    }


    public List<String> packageFilter = []

    void joinPakageFilter() {
        List<String> pakages = config.pakages
        for (String pakage : pakages) {
            packageFilter.add(pakage.replace('.', File.separator))
        }
    }

    boolean shouldProcess(String path) {
        if (path == null || !path.endsWith(".class")) return false
        for (String target : packageFilter) {
            if (path.contains(target)) {
                System.out.println("Insert class >> " + path)
                return true
            }
        }
        return false
    }

    void injectClass(File file) {
        byte[] bytes = injectClass(new FileInputStream(file))
        if (bytes != null) {
            FileUtils.writeByteArrayToFile(file, bytes)
        }
    }

    byte[] injectClass(InputStream fis) {
        try {
            ClassReader cr = new ClassReader(fis);
            ClassWriter cw = new ClassWriter(cr, 0);
            cr.accept(new ExpClassAdapter(cw, config)
                    , ClassReader.EXPAND_FRAMES)
            return cw.toByteArray()
        } catch (Exception e) {
            e.printStackTrace()
        }
        return null
    }

    /* 对本地变量和操作数栈的大小设置受ClassWriter的flag取值影响：
     （1）new ClassWriter(0),表明需要手动计算栈帧大小、本地变量和操作数栈的大小；
     （2）new ClassWriter(ClassWriter.COMPUTE_MAXS)需要自己计算栈帧大小，但本地变量与操作数已自动计算好，当然也可以调用visitMaxs方法，只不过不起作用，参数会被忽略；
     （3）new ClassWriter(ClassWriter.COMPUTE_FRAMES)栈帧本地变量和操作数栈都自动计算，不需要调用visitFrame和visitMaxs方法，即使调用也会被忽略。
     这些选项非常方便，但会有一定的开销，使用COMPUTE_MAXS会慢10%，使用COMPUTE_FRAMES会慢2倍。*/
}