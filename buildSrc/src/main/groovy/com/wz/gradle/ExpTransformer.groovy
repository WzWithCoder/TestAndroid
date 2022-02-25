package com.wz.gradle;

import com.android.build.api.transform.*;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

class ExpTransformer extends Transform {
    private Project project
    private InjectClass injectClass;

    ExpTransformer(Project project) {
        this.project = project
        injectClass = new InjectClass(project)
    }

    @Override
    String getName() {
        return "exception"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        //return TransformManager.SCOPE_FULL_PROJECT
        return ImmutableSet.of(
                QualifiedContent.Scope.PROJECT,
                QualifiedContent.Scope.SUB_PROJECTS
        )
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider, boolean isIncremental)
            throws IOException, TransformException, InterruptedException {
        injectClass.joinPakageFilter()
        inputs.each { TransformInput input ->
            input.directoryInputs.each { DirectoryInput dirInput ->
                def dest = outputProvider.getContentLocation(dirInput.name,
                        dirInput.contentTypes, dirInput.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(dirInput.file, dest)
                injectClass.injectDir(dest.getAbsolutePath())
            }

            input.jarInputs.each { JarInput jarInput ->
                def jarPath = jarInput.file.getAbsolutePath()
                def jarName = DigestUtils.md5Hex(jarPath)
                File dest = outputProvider.getContentLocation(jarName,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                FileUtils.copyFile(jarInput.file, dest)

                if (shouldProcessPreDexJar(jarPath)) {
                    injectClass.injectJar(dest.getAbsolutePath())
                }
            }
        }
    }

    static boolean shouldProcessPreDexJar(String path) {
        return path != null && path.endsWith(".jar") &&
                !path.contains("android/support/") &&
                !path.contains("/android/m2repository")
    }
}
