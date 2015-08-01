
LOCAL_PATH :=$(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := ChessServer

LOCAL_SRC_FILES := com_example_jni_ChessServerJNI.cpp

include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)

LOCAL_MODULE := TwoAI

LOCAL_SRC_FILES := com_example_jni_ChessJNI.cpp

include $(BUILD_SHARED_LIBRARY)