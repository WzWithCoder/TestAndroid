package com.wz.gradle;

import org.apache.http.util.TextUtils;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wangzheng on 15-12-4.
 */
public final class ExpMethodAdapter extends AdviceAdapter {
    // A catch may contain multiple exceptions,
    // catch(IndexOutOfBoundsException | Exception e)
    private HashMap<Label, ArrayList<String>> targetHandles;
    private ExpConfig mConfig;

    protected ExpMethodAdapter(MethodVisitor methodVisitor
            , int access, String name, String desc, ExpConfig config) {
        super(Opcodes.ASM5, methodVisitor, access, name, desc);

        this.mConfig = config;
        targetHandles = new HashMap<>();
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end
            , Label handle, String exception) {
        if (!isExpIgnore(exception)) {
            ArrayList<String> handles =
                    targetHandles.get(handle);
            if (handles == null) {
                handles = new ArrayList<>();
                targetHandles.put(handle, handles);
            }
            handles.add(exception);
        }
        super.visitTryCatchBlock(start, end, handle, exception);
    }

    @Override
    public void visitLabel(Label label) {
        super.visitLabel(label);

        final ArrayList<String> exceptions =
                targetHandles.get(label);
        if (label == null) return;
        if (exceptions == null) return;
        if (exceptions.isEmpty()) return;

        //异常处理代码块 开始和结束标签声明
        Label start = new Label();
        Label end = new Label();

        //捕获的是目标异常，跳转到异常处理开始处
        for (String exp : exceptions) {
            compareInstance(exp, start);
        }
        //未匹配到目标异常，跳转到异常处理结束处
        visitJumpInsn(GOTO, end);

        //异常处理代码块
        visitLabel(start);
        dup();
        invokeHookMethod();
        visitLabel(end);
    }

    private void compareInstance(String type, Label to) {
        dup();
        instanceOf(Type.getObjectType(type));
        visitJumpInsn(IFNE, to);
    }

    private void invokeHookMethod() {
        if (mConfig == null) return;
        Type targetClass = Type.getObjectType(
                mConfig.processor);
        final Method method = new Method(
                mConfig.method,
                mConfig.signature);
        invokeStatic(targetClass, method);
    }

    private boolean isExpIgnore(String exception) {
        if (TextUtils.isEmpty(exception)) {
            return true;
        }
        List<String> exceptions = mConfig.exception;
        if (exceptions == null) {
            return false;
        }
        if (exceptions.contains(exception)) {
            return true;
        }
        return false;
    }
}
