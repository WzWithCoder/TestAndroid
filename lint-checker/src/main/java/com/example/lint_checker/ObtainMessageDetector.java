package com.example.lint_checker;

import com.android.annotations.Nullable;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Detector;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.JavaContext;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.intellij.psi.PsiMethod;

import org.jetbrains.uast.UCallExpression;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class ObtainMessageDetector extends Detector implements Detector.UastScanner {

    private static final Class<? extends Detector> DETECTOR_CLASS = ObtainMessageDetector.class;
    private static final EnumSet<Scope> DETECTOR_SCOPE = Scope.JAVA_FILE_SCOPE;

    public static final Issue ISSUE = Issue.create(
            "MessageObtainUseError",
            "不建议直接new Message()",
            "建议调用{handler.obtainMessage} or {Message.Obtain()}获取缓存的message",
            Category.PERFORMANCE,
            9,
            Severity.WARNING,
            new Implementation(DETECTOR_CLASS, DETECTOR_SCOPE)
    );


    @Nullable
    @Override
    public List<String> getApplicableConstructorTypes() {
        return Collections.singletonList("android.os.Message");
    }

    @Override
    public void visitConstructor(JavaContext context, UCallExpression node, PsiMethod constructor) {
        System.out.println("cnkjdnckndsjcnsdkcnsjdnckjsdnckjsndcnjskd");
        context.report(ISSUE, node, context.getLocation(node), "建议调用{handler.obtainMessage} or {Message.Obtain()}获取缓存的message");
    }
}