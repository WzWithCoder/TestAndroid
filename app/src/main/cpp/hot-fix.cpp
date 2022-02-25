#include <jni.h>
#include <string>
#include <android/log.h>

#ifdef __cplusplus
extern "C" {
#endif

#define JNI_API_DEF(f) Java_com_example_wangzheng_HotFixMethod_##f

size_t methodSize = 0;
static jint sizeOfMethod(JNIEnv *env) {
    if (methodSize != 0) return methodSize;
    jclass clazz = env->FindClass("com/example/wangzheng/HotFixMethod$NativeMethodSize");
    size_t jmethodId1 = (size_t)env->GetStaticMethodID(clazz, "method1", "()V");
    size_t jmethodId2 = (size_t)env->GetStaticMethodID(clazz, "method2", "()V");
    methodSize = jmethodId2 - jmethodId1;
    __android_log_print(ANDROID_LOG_INFO, "hot-fix", "initMethodSize : %d , %d, %d" , jmethodId1, jmethodId2, methodSize);
    return methodSize;
}

JNIEXPORT void JNICALL JNI_API_DEF(replace)(JNIEnv *env
        , jclass jclazz, jobject newMethod, jobject oldMethod) {
    jint size = sizeOfMethod(env);
    __android_log_print(ANDROID_LOG_INFO, "hot-fix", "befor replaceMethod");
    jmethodID newMethodId = env->FromReflectedMethod(newMethod);
    jmethodID oldMethodId = env->FromReflectedMethod(oldMethod);
    memcpy(oldMethodId, newMethodId, size);
    __android_log_print(ANDROID_LOG_INFO, "hot-fix", "after replaceMethod %d", size);
}

#ifdef __cplusplus
}
#endif