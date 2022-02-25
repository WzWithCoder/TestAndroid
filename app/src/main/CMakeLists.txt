cmake_minimum_required(VERSION 3.4.1)

add_library(native-lib
        SHARED
        ${CMAKE_SOURCE_DIR}/jni/native-lib.cpp
        ${CMAKE_SOURCE_DIR}/jni/MediaContext.cpp
        ${CMAKE_SOURCE_DIR}/jni/MediaPlayer.cpp
        ${CMAKE_SOURCE_DIR}/jni/BlockingQueue.cpp
        ${CMAKE_SOURCE_DIR}/jni/OpenGLKit.cpp
        ${CMAKE_SOURCE_DIR}/jni/OpenSLKit.cpp
        ${CMAKE_SOURCE_DIR}/jni/EGLKit.cpp
        ${CMAKE_SOURCE_DIR}/jni/ShaderKit.cpp
        ${CMAKE_SOURCE_DIR}/jni/FFmpegKit.cpp
        ${CMAKE_SOURCE_DIR}/jni/Mutex.cpp
        ${CMAKE_SOURCE_DIR}/jni/daemon-process.c)

set(LIB_PATH ${CMAKE_SOURCE_DIR}/jniLibs/${ANDROID_ABI})

add_library(avutil-54
        SHARED
        IMPORTED)
set_target_properties(avutil-54
        PROPERTIES IMPORTED_LOCATION
        ${LIB_PATH}/libavutil-54.so)

add_library(avcodec-56
        SHARED
        IMPORTED)
set_target_properties(avcodec-56
        PROPERTIES IMPORTED_LOCATION
        ${LIB_PATH}/libavcodec-56.so)

add_library(avfilter-5
        SHARED
        IMPORTED)
set_target_properties(avfilter-5
        PROPERTIES IMPORTED_LOCATION
        ${LIB_PATH}/libavfilter-5.so)

add_library(avformat-56
        SHARED
        IMPORTED)
set_target_properties(avformat-56
        PROPERTIES IMPORTED_LOCATION
        ${LIB_PATH}/libavformat-56.so)

add_library(avdevice-56
        SHARED
        IMPORTED)
set_target_properties(avdevice-56
        PROPERTIES IMPORTED_LOCATION
        ${LIB_PATH}/libavdevice-56.so)

add_library(swresample-1
        SHARED
        IMPORTED)
set_target_properties(swresample-1
        PROPERTIES IMPORTED_LOCATION
        ${LIB_PATH}/libswresample-1.so)

add_library(swscale-3
        SHARED
        IMPORTED)
set_target_properties(swscale-3
        PROPERTIES IMPORTED_LOCATION
        ${LIB_PATH}/libswscale-3.so)

add_library(postproc-53
        SHARED
        IMPORTED)
set_target_properties(postproc-53
        PROPERTIES IMPORTED_LOCATION
        ${LIB_PATH}/libpostproc-53.so)

add_library(yuv
        SHARED
        IMPORTED)
set_target_properties(yuv
        PROPERTIES IMPORTED_LOCATION
        ${LIB_PATH}/libyuv.so)

include_directories(${CMAKE_SOURCE_DIR}/jni/ffmpeg)
include_directories(${CMAKE_SOURCE_DIR}/jni/yuv)
include_directories(${CMAKE_SOURCE_DIR}/jni/)

#app\.cxx\cmake\armeabi\CMakeFiles\build_output.txt
message(STATUS "-------------- "${CMAKE_SOURCE_DIR})

target_link_libraries(native-lib
        yuv
        avutil-54
        avcodec-56
        avfilter-5
        avformat-56
        avdevice-56
        swresample-1
        swscale-3
        postproc-53
        #system library
        EGL
        GLESv2
        OpenSLES
        log
        android)