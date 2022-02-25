package com.example;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.net.URISyntaxException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

public class MainTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader classLoader, String className, Class<?> classBeingRedefined
            , ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (mClasses.contains(className)) {
            return matchBusiness(className, classfileBuffer);
        } else {
            return classfileBuffer;
        }
    }

    private byte[] matchBusiness(String className, byte[] classfileBuffer) {
        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        ClassVisitor classAdapter = null;
        try {
            if (Main.equals(className)) {
                classAdapter = hookDexerMain(cw);
            } else if (ProcessBuilder.equals(className)) {
                classAdapter = hookProcessBuilder(cw);
            }
            cr.accept(classAdapter, ClassReader.SKIP_FRAMES);
        } catch (Exception e) {
            e.printStackTrace();
            return classfileBuffer;
        }
        byte[] bs = cw.toByteArray();
        writeBytesToFile(className.replace("/", ""), bs);
        return bs;
    }

    public static void writeBytesToFile(String name, byte[] bs) {
        OutputStream out = null;
        InputStream is = null;
        try {
            out = new FileOutputStream("d://" + name + ".class");
            is = new ByteArrayInputStream(bs);
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = is.read(buff)) != -1) {
                out.write(buff, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            try {
                is.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //ProcessBuilder.start()  command
    private ClassVisitor hookProcessBuilder(final ClassWriter cw) {
        return new ClassVisitor(Opcodes.ASM5, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor visitor = super.visitMethod(access, name, desc, signature, exceptions);
                //public Process start() throws IOException
                if ("start".equals(name) && "()Ljava/lang/Process;".equals(desc)) {
                    return new StartMethodVisitor(visitor);
                }
                return visitor;
            }
        };
    }

    class StartMethodVisitor extends MethodVisitor {
        private boolean isMarked = false;

        public StartMethodVisitor(MethodVisitor mv) {
            super(Opcodes.ASM5, mv);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            if (Type.getDescriptor(Transformed.class).equals(desc)) {
                isMarked = true;
            }
            return super.visitAnnotation(desc, visible);
        }

        @Override
        public void visitCode() {
            if (!isMarked) {
                insert();
            }
            super.visitCode();
        }

        private void insert() {
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(21, l0);
            mv.visitInsn(Opcodes.ACONST_NULL);
            mv.visitVarInsn(Opcodes.ASTORE, 1);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLineNumber(22, l1);
            mv.visitInsn(Opcodes.ICONST_0);
            mv.visitVarInsn(Opcodes.ISTORE, 2);
            Label l2 = new Label();
            mv.visitLabel(l2);
            Label l3 = new Label();
            mv.visitJumpInsn(Opcodes.GOTO, l3);
            Label l4 = new Label();
            mv.visitLabel(l4);
            mv.visitLineNumber(23, l4);
            mv.visitFrame(Opcodes.F_APPEND, 2, new Object[]{"java/io/File", Opcodes.INTEGER}, 0, null);
            mv.visitTypeInsn(Opcodes.NEW, "java/io/File");
            mv.visitInsn(Opcodes.DUP);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, ProcessBuilder, "command", "Ljava/util/List;");
            mv.visitVarInsn(Opcodes.ILOAD, 2);
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "get", "(I)Ljava/lang/Object;");
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/String");
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/lang/String;)V");
            mv.visitVarInsn(Opcodes.ASTORE, 1);
            Label l5 = new Label();
            mv.visitLabel(l5);
            mv.visitLineNumber(24, l5);
            mv.visitInsn(Opcodes.ACONST_NULL);
            mv.visitVarInsn(Opcodes.ASTORE, 3);
            Label l6 = new Label();
            mv.visitLabel(l6);
            mv.visitLineNumber(25, l6);
            mv.visitLdcInsn("dx.bat");
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/File", "getName", "()Ljava/lang/String;");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "toLowerCase", "()Ljava/lang/String;");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z");
            Label l7 = new Label();
            mv.visitJumpInsn(Opcodes.IFEQ, l7);
            Label l8 = new Label();
            mv.visitLabel(l8);
            mv.visitLineNumber(26, l8);
            mv.visitLdcInsn("-Jjavaagent:" + getAgentPath());
            mv.visitVarInsn(Opcodes.ASTORE, 3);
            Label l9 = new Label();
            mv.visitLabel(l9);
            mv.visitLineNumber(27, l9);
            Label l10 = new Label();
            mv.visitJumpInsn(Opcodes.GOTO, l10);
            mv.visitLabel(l7);
            mv.visitFrame(Opcodes.F_APPEND, 1, new Object[]{"java/lang/String"}, 0, null);
            mv.visitLdcInsn("java.exe");
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/File", "getName", "()Ljava/lang/String;");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "toLowerCase", "()Ljava/lang/String;");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z");
            mv.visitJumpInsn(Opcodes.IFEQ, l10);
            Label l11 = new Label();
            mv.visitLabel(l11);
            mv.visitLineNumber(28, l11);
            mv.visitLdcInsn("-javaagent:" + getAgentPath());
            mv.visitVarInsn(Opcodes.ASTORE, 3);
            mv.visitLabel(l10);
            mv.visitLineNumber(30, l10);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(Opcodes.ALOAD, 3);
            Label l12 = new Label();
            mv.visitJumpInsn(Opcodes.IFNULL, l12);
            Label l13 = new Label();
            mv.visitLabel(l13);
            mv.visitLineNumber(31, l13);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, ProcessBuilder, "command", "Ljava/util/List;");
            mv.visitVarInsn(Opcodes.ILOAD, 2);
            mv.visitInsn(Opcodes.ICONST_1);
            mv.visitInsn(Opcodes.IADD);
            mv.visitVarInsn(Opcodes.ALOAD, 3);
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "add", "(ILjava/lang/Object;)V");
            Label l14 = new Label();
            mv.visitLabel(l14);
            mv.visitLineNumber(32, l14);
            Label l15 = new Label();
            mv.visitJumpInsn(Opcodes.GOTO, l15);
            mv.visitLabel(l12);
            mv.visitLineNumber(22, l12);
            mv.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);
            mv.visitIincInsn(2, 1);
            mv.visitLabel(l3);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(Opcodes.ILOAD, 2);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, ProcessBuilder, "command", "Ljava/util/List;");
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/List", "size", "()I");
            mv.visitJumpInsn(Opcodes.IF_ICMPLT, l4);
            mv.visitLabel(l15);
            mv.visitLineNumber(35, l15);
            mv.visitFrame(Opcodes.F_CHOP, 1, null, 0, null);

            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitFieldInsn(Opcodes.GETFIELD, ProcessBuilder, "command", "Ljava/util/List;");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;");
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
        }

        @Override
        public void visitEnd() {
            if (!isMarked) {
                super.visitAnnotation(Type.getDescriptor(Transformed.class), false);
            }
            super.visitEnd();
        }
    }

    //private boolean processClass(String name, byte[] bytes)
    private ClassVisitor hookDexerMain(final ClassWriter mv) {
        return new ClassVisitor(Opcodes.ASM5, mv) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor visitor = super.visitMethod(access, name, desc, signature, exceptions);
                if ("processClass".equals(name) && "(Ljava/lang/String;[B)Z;".equals(desc)) {
                    System.out.println("name:" + name + ";desc:" + desc + ";signature:" + signature
                            + ";exceptions:" + (exceptions == null ? "null" : exceptions.toString()));
                    return new ProcessClassMethodVisitor(visitor);
                }
                return visitor;
            }
        };
    }

    class ProcessClassMethodVisitor extends MethodVisitor {
        private boolean isMarked = false;

        public ProcessClassMethodVisitor(MethodVisitor visitor) {
            super(Opcodes.ASM5, visitor);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            if (Type.getDescriptor(Transformed.class).equals(desc)) {
                isMarked = true;
            }
            return super.visitAnnotation(desc, visible);
        }

        @Override
        public void visitCode() {
            visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            visitLdcInsn("-88888888888888888888888888888888888888888888888888");
            visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
//
//            visitVarInsn(Opcodes.ALOAD, 1);
//            visitVarInsn(Opcodes.ALOAD, 2);
//            visitMethodInsn(Opcodes.INVOKESTATIC, "com/example/TestTransformer", "processClass", "(Ljava/lang/String;[B)V", false);
            super.visitCode();
        }

        @Override
        public void visitEnd() {
            if (!isMarked) {
                super.visitAnnotation(Type.getDescriptor(Transformed.class), false);
            }
            super.visitEnd();
        }
    }

    public static String getAgentPath() {
        try {
            return new File(TestAgent.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI().getPath()).getAbsolutePath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static List<String> mClasses = new ArrayList<String>() {
        {
            add(ProcessBuilder);
            add(Main);
        }
    };

    public static boolean transforms(Class<?> klass) {
        String internalName = klass.getName().replace('.', '/');
        return mClasses.contains(internalName);
    }

    public static final String Main = "com/android/dx/command/dexer/Main";
    public static final String ProcessBuilder = "java/lang/ProcessBuilder";
}