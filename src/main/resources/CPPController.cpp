#include <jni.h>
#include "Controller_Java_CPPController.h"
// Include the background logic headers from src/CPP/ here
#include "Engine.h"

// Global pointer needed to track the active Java environment for live updates
JNIEnv* globalEnv = nullptr;

// =========================================================================
//  HOW TO ADD A NEW INT CONSTANT:
// 1. Change "NETWORK_PORT" to the Java variable's exact name.
// 2. Pass 'nativePort' to whatever C++ module needs to use it.
// 3. NOTE: For inner classes, use "Constants$GeneralConstants" or "Constants$TaskConstants"
// =========================================================================
jint getLiveIntField(const char* innerClassName, const char* fieldName) {
    if (globalEnv == nullptr) return 0;
    jclass constantsClass = globalEnv->FindClass(innerClassName);
    if (constantsClass == nullptr) return 0;

    jfieldID portId = globalEnv->GetStaticFieldID(constantsClass, fieldName, "I");
    return globalEnv->GetStaticIntField(constantsClass, portId);
}

// =========================================================================
//  HOW TO ADD A NEW STRING CONSTANT:
// 1. Change "ENCRYPTION_ALGORITHM" to the Java variable's name.
// 2. Keep the "Ljava/lang/String;" signature exactly as-is.
// =========================================================================
std::string getLiveStringField(const char* innerClassName, const char* fieldName) {
    if (globalEnv == nullptr) return "";
    jclass constantsClass = globalEnv->FindClass(innerClassName);
    if (constantsClass == nullptr) return "";

    jfieldID algoId = globalEnv->GetStaticFieldID(constantsClass, fieldName, "Ljava/lang/String;");
    jstring jAlgoStr = (jstring)globalEnv->GetStaticObjectField(constantsClass, algoId);

    // Safe string conversion chunk (Keep this block intact for every string)
    const char* rawAlgo = globalEnv->GetStringUTFChars(jAlgoStr, nullptr);
    std::string nativeAlgo(rawAlgo);
    globalEnv->ReleaseStringUTFChars(jAlgoStr, rawAlgo);

    return nativeAlgo;
}

JNIEXPORT void JNICALL Java_Controller_Java_CPPController_syncSystemConstants(JNIEnv *env, jobject obj) {
    // Step 1: Find the target Constants class file (We check the base class here)
    jclass constantsClass = env->FindClass("Constants");
    if (constantsClass == nullptr) return;

    // Cache the active environment context so live helpers can safely access Java memory dynamically
    globalEnv = env;

    // Fetch fresh live snapshots immediately upon initial initialization pass
    // Updated: Passing the path to GeneralConstants inner class layout
    jint nativePort = getLiveIntField("Constants$GeneralConstants", "NETWORK_PORT");
    std::string nativeAlgo = getLiveStringField("Constants$GeneralConstants", "ENCRYPTION_ALGORITHM");

    // Quick Reference for Type Signatures
    // When adding new types of constants,
    // the letter code at the end of GetStaticFieldID changes based on this standard chart:
    // I = int
    // Z = boolean
    // D = double
    // J = long
    // Ljava/lang/String; = String

    // =========================================================================
    // Step 2: Route the extracted variables straight out to the background files
    // =========================================================================
    CoreEngine::configure(nativePort, nativeAlgo);
}

// Added to fulfill the header file requirement mapping
JNIEXPORT void JNICALL Java_Controller_Java_CPPController_executeCPPLogic(JNIEnv *env, jobject obj) {
}

JNIEXPORT void JNICALL Java_Controller_Java_CPPController_executePrintHelloWorld(JNIEnv *env, jobject obj) {
    std::cout << "Hello World";
}
