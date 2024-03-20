#include <jni.h>
#include <string>
#include <android/log.h>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_nativelib_NativeLib_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
   return env->NewStringUTF(hello.c_str());
}

static jstring query_by_command(
        JNIEnv *env,
        jobject clazz,
        jstring modemType,
        jstring cmd,
        jstring prefix) {
    char output[100] = {0};

    return env->NewStringUTF(output);
}

/*
 * Class:     com_tdtech_lte_ProjectMenuAssist_batemodem_ProjectMenu
 * Method:    setByCommands
 * Signature: (Ljava/lang/String;[Ljava/lang/String;)Z
 */
static jboolean set_by_commands(JNIEnv *env, jobject clazz, jstring modemType,jobjectArray cmd) {
    int ret = 0;

    return (ret == 0);
}

static const char *classPathName = "com/example/nativelib/NativeLib";//native接口定义类（路径+class名）

static JNINativeMethod methods[] = {
        {"queryByCommand", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", (void *) query_by_command},
        {"setByCommands",  "(Ljava/lang/String;[Ljava/lang/String;)Z",                                   (void *) set_by_commands},
};
/*
 * Register several native methods for one class.
 */
static int registerNativeMethods(JNIEnv* env, const char* className,
                                 JNINativeMethod* gMethods, int numMethods) {
    jclass clazz;

    clazz = env->FindClass(className);
    if (clazz == NULL) {
        //ALOGE("Native registration unable to find class '%s'", className);
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        //ALOGE("RegisterNatives failed for '%s'", className);
        return JNI_FALSE;
    }

    return JNI_TRUE;
}


/*
 * Register native methods for all classes we know about.
 *
 * returns JNI_TRUE on success.
 */
static int registerNatives(JNIEnv* env) {
    if (!registerNativeMethods(env, classPathName,
                               methods, sizeof(methods) / sizeof(methods[0]))) {
        return JNI_FALSE;
    }

    return JNI_TRUE;
}

// ----------------------------------------------------------------------------
/*
 * This is called by the VM when the shared library is first loaded.
 */

typedef union {
    JNIEnv* env;
    void* venv;
} UnionJNIEnvToVoid;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {

    UnionJNIEnvToVoid uenv;
    uenv.venv = NULL;
    jint result = -1;
    JNIEnv* env = NULL;

    //ALOGI("JNI_OnLoad");

    if (vm->GetEnv(&uenv.venv, JNI_VERSION_1_4) != JNI_OK) {
        //ALOGE("ERROR: GetEnv failed");
        return JNI_FALSE;
    }
    env = uenv.env;

    if (registerNatives(env) != JNI_TRUE) {
        //ALOGE("ERROR: registerNatives failed");
        return JNI_FALSE;
    }

    result = JNI_VERSION_1_4;
    return result;
}
