LOCAL_PATH := $(call my-dir)

include $(CLAER_VARS)

LOCAL_MODULE := batch_sendevent
LOCAL_MODULE_FILENAME:= batch_sendevent

LOCAL_SRC_FILES := sendevent.c

include $(BUILD_EXECUTABLE)
