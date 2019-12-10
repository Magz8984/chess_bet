

#include <jni.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/stat.h>
#include <sys/time.h>
#include <sys/resource.h>

extern "C" JNIEXPORT jboolean JNICALL Java_stockfish_Engine_chmod (JNIEnv *env, jclass, jstring jExePath) {
    const char* exePath = env->GetStringUTFChars(jExePath, NULL);
    if (!exePath)
        return false;
    bool ret = chmod(exePath, 0755) == 0;
    env->ReleaseStringUTFChars(jExePath, exePath);
    return ret;
}


extern "C" JNIEXPORT void JNICALL Java_stockfish_Engine_changePriority (JNIEnv *env, jclass, jint pid, jint prio) {
    setpriority (PRIO_PROCESS, pid, prio);
}

