#include <jni.h>
#include <string>

#include "H264Parser.h"

#include <android/log.h>

#define JNI_TAG "ParseH264"


#define PHLOGE(format, ...) __android_log_print(ANDROID_LOG_ERROR, JNI_TAG, format, ##__VA_ARGS__)
HH::ParserH264 parser;
int dst_width=0;
int dst_height=0;
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_rzrtc_parseh264_H264Utils_getWidth_1Height_1fromPacket(JNIEnv *env, jobject thiz,
                                                                   jbyteArray packets,
                                                                   jint length) {
    bool sucess=false;
    jbyte *content_array = (env)->GetByteArrayElements(packets, NULL);
    int width=0;
    int height=0;
    bool result=parser.parserH264(reinterpret_cast<const uint8_t *>(content_array), length, width, height);
    if (result) {
        dst_width = width;
        dst_height=height;
        sucess=true;
    }
    (env)->ReleaseByteArrayElements(packets, content_array, 0);
    return sucess;
}extern "C"
JNIEXPORT jint JNICALL
Java_com_rzrtc_parseh264_H264Utils_getWidth(JNIEnv *env, jobject thiz) {
    return dst_width;
}extern "C"
JNIEXPORT jint JNICALL
Java_com_rzrtc_parseh264_H264Utils_getHeight(JNIEnv *env, jobject thiz) {
    return dst_height;
}