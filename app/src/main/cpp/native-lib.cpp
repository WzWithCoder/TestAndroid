#include <jni.h>
#include <string>
#include <android/log.h>
#include <sys/mman.h>
#include "xhook/xhook.h"
#include <sstream>
#include<sys/types.h>
#include<sys/stat.h>
#include<fcntl.h>

//第一步：导入Socket编程的标准库
//这个标准库：linux数据类型(size_t、time_t等等)

#include <sys/types.h>
//提供socket函数以及数据结构
#include <sys/socket.h>

//数据解构(sockaddr_in)
#include <netinet/in.h>
//IP地址的转换函数
#include <arpa/inet.h>

#ifdef __cplusplus
extern "C" {
#endif

#define JNI_API_DEF(f) Java_com_example_wangzheng_ui_MainActivity_##f

jobject mJobject = NULL;
JavaVM *mJavaVM = NULL;

static void *nativeHello() {}
const static JNINativeMethod nativeMethods[] = {
        {"nativeHello",
                "()V",
                (void *) nativeHello},
};

static int registerNativeMethods(JNIEnv *env, const char *className, JNINativeMethod *nativeMethods) {
    jclass clazz;
    //找到声明native方法的类
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    //注册函数 参数：java类 所要注册的函数数组 注册函数的个数
    int methodsNum = sizeof(nativeMethods) / sizeof(nativeMethods[0]);
    if (env->RegisterNatives(clazz, nativeMethods, methodsNum) < 0) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    mJavaVM = vm;
    JNIEnv *env = NULL;
    vm->AttachCurrentThread(&env, 0);
    //registerNativeMethods(env, );
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved) {
    JNIEnv *env = NULL;
    vm->AttachCurrentThread(&env, 0);
    env->DeleteGlobalRef(mJobject);
}

static void invokeJavaMethod(const char* bytes) {
    JNIEnv *env = NULL;
    mJavaVM->AttachCurrentThread(&env, 0);

    jclass targetClass = env->GetObjectClass(mJobject);
    jmethodID methodId = env->GetMethodID(targetClass
            , "nativeInvokeHandle"
            , "(Ljava/lang/String;)V");
    env->CallVoidMethod(mJobject, methodId, env->NewStringUTF(bytes));

    //mJavaVM->DetachCurrentThread();
}

static void *(*old_mmap)(void *start, size_t length, int prot, int flags, int fd, off_t offset);
static void *new_mmap(void *start, size_t length, int prot, int flags, int fd, off_t offset) {
    __android_log_print(ANDROID_LOG_INFO, "xhook", "hook befor new-mmap length : %x", length);
    void *address = old_mmap(start, length, prot, flags, fd, offset);
    __android_log_print(ANDROID_LOG_INFO, "xhook", "hook after new-mmap address : %x", address);

    std::stringstream ss;
    ss << "hook mmap [" << address << "-" << length << "]";
    const char *bytes = ss.str().c_str();
    invokeJavaMethod(bytes);
    return address;
}

static int (*old_open)(const char *pathname, int flags, mode_t mode);
static int new_open(const char *pathname, int flags, mode_t mode) {
    __android_log_print(ANDROID_LOG_INFO, "xhook", "hook befor new-open : %s", pathname);
    int result = old_open(pathname, flags, mode);
    __android_log_print(ANDROID_LOG_INFO, "xhook", "hook after new-open : %s", pathname);
    std::stringstream ss;
    ss << "hook open [" << pathname << "]";
    const char *bytes = ss.str().c_str();
    invokeJavaMethod(bytes);
    return result;
}

static int (*old_connect)(int __fd, const struct sockaddr* __addr, socklen_t __addr_length);
static int new_connect(int __fd, const struct sockaddr* __addr, socklen_t __addr_length) {
    __android_log_print(ANDROID_LOG_INFO, "xhook", "hook befor new-connect : %s" , __addr->sa_data);
    int result = old_connect(__fd, __addr, __addr_length);
    std::stringstream ss;
    ss << "hook connect [" << __addr->sa_data << "]";
    const char *bytes = ss.str().c_str();
    invokeJavaMethod(bytes);
    return result;
}

JNIEXPORT jstring JNICALL JNI_API_DEF(initXhookFromJNI)(JNIEnv *env, jobject jobj) {
    mJobject = env->NewGlobalRef(jobj);
    xhook_register(".*\\.so$", "connect", (void *) new_connect, (void **) (&old_connect));
    //xhook_register(".*\\.so$", "open", (void *) new_open, (void **) (&old_open));
    //xhook_register("^/system/.*$", "mmap", (void *) new_mmap, (void **) (&old_mmap));
    //xhook_ignore("libnative", "mmap");
    xhook_refresh(1);

    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

#ifdef __cplusplus
}
#endif