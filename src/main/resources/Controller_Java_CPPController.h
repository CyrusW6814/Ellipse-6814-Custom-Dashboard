#include <jni.h>

#ifndef _Included_Controller_Java_CPPController
#define _Included_Controller_Java_CPPController
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT void JNICALL Java_Controller_Java_CPPController_syncSystemConstants(JNIEnv *env, jobject obj);
JNIEXPORT void JNICALL Java_Controller_Java_CPPController_executeCPPLogic(JNIEnv *env, jobject obj);
JNIEXPORT void JNICALL Java_Controller_Java_CPPController_executePrintHelloWorld(JNIEnv *env, jobject obj);

#ifdef __cplusplus
}
#endif
#endif
