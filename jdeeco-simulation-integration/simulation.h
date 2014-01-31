/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class cz_cuni_mff_d3s_deeco_simulation_Simulation */

#ifndef _Included_cz_cuni_mff_d3s_deeco_simulation_Simulation
#define _Included_cz_cuni_mff_d3s_deeco_simulation_Simulation
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL _JNI_OnLoad(JavaVM *, void *);

/*
 * Class:     cz_cuni_mff_d3s_deeco_simulation_Simulation
 * Method:    getCurrentTime
 * Signature: ()D
 */
JNIEXPORT jdouble JNICALL _Java_cz_cuni_mff_d3s_deeco_simulation_Simulation_nativeGetCurrentTime
  (JNIEnv *, jobject);

/*
 * Class:     cz_cuni_mff_d3s_deeco_simulation_Simulation
 * Method:    initializeHost
 * Signature: (Ljava/lang/Object;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL _Java_cz_cuni_mff_d3s_deeco_simulation_Simulation_nativeRegister
  (JNIEnv *, jobject, jobject, jstring);

/*
 * Class:     cz_cuni_mff_d3s_deeco_simulation_Simulation
 * Method:    broadcastPacket
 * Signature: (Ljava/lang/String;[B)V
 */
JNIEXPORT void JNICALL _Java_cz_cuni_mff_d3s_deeco_simulation_Simulation_nativeSendPacket
  (JNIEnv *, jobject, jstring, jbyteArray, jstring);

/*
 * Class:     cz_cuni_mff_d3s_deeco_simulation_Simulation
 * Method:    run
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL _Java_cz_cuni_mff_d3s_deeco_simulation_Simulation_nativeRun
  (JNIEnv *, jobject, jstring, jstring);

/*
 * Class:     cz_cuni_mff_d3s_deeco_simulation_Simulation
 * Method:    callAt
 * Signature: (JLjava/lang/String;)V
 */
JNIEXPORT void JNICALL _Java_cz_cuni_mff_d3s_deeco_simulation_Simulation_nativeCallAt
  (JNIEnv *, jobject, jdouble, jstring);

/*
 * Class:     cz_cuni_mff_d3s_deeco_simulation_Simulation
 * Method:    broadcastPacket
 * Signature: (Ljava/lang/String)Z
 */
JNIEXPORT jboolean JNICALL _Java_cz_cuni_mff_d3s_deeco_simulation_Simulation_nativeIsPositionInfoAvailable
  (JNIEnv *, jobject, jstring);

/*
 * Class:     cz_cuni_mff_d3s_deeco_simulation_Simulation
 * Method:    broadcastPacket
 * Signature: (Ljava/lang/String)Z
 */
JNIEXPORT jdouble JNICALL _Java_cz_cuni_mff_d3s_deeco_simulation_Simulation_nativeGetPositionX
  (JNIEnv *, jobject, jstring);

/*
 * Class:     cz_cuni_mff_d3s_deeco_simulation_Simulation
 * Method:    broadcastPacket
 * Signature: (Ljava/lang/String)Z
 */
JNIEXPORT jdouble JNICALL _Java_cz_cuni_mff_d3s_deeco_simulation_Simulation_nativeGetPositionY
  (JNIEnv *, jobject, jstring);

/*
 * Class:     cz_cuni_mff_d3s_deeco_simulation_Simulation
 * Method:    broadcastPacket
 * Signature: (Ljava/lang/String)Z
 */
JNIEXPORT jdouble JNICALL _Java_cz_cuni_mff_d3s_deeco_simulation_Simulation_nativeGetPositionZ
  (JNIEnv *, jobject, jstring);

#ifdef __cplusplus
}
#endif
#endif
