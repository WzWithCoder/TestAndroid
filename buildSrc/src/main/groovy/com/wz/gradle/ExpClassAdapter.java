package com.wz.gradle;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by wangzheng on 15-12-4.
 */
public class ExpClassAdapter extends ClassVisitor {
    private ExpConfig config;

    public ExpClassAdapter(ClassVisitor classVisitor, ExpConfig config) {
        super(Opcodes.ASM5, classVisitor);
        this.config = config;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        return new ExpMethodAdapter(
                super.visitMethod(access, name, desc, signature, exceptions),
                access, name, desc,
                config);
    }
}
