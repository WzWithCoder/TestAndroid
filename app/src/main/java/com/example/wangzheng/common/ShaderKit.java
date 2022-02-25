package com.example.wangzheng.common;

import android.content.res.Resources;
import android.opengl.GLES20;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Create by wangzheng on 2018/9/21
 */
public class ShaderKit {
    public static String id2content(Resources resources, int rawId) {
        InputStream inputStream = resources.openRawResource(rawId);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream));
        StringBuffer buffer = new StringBuffer();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

    public static int createProgram(String vertexSource, String segmentSource) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) {
            return 0;
        }
        int segmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, segmentSource);
        if (segmentShader == 0) {
            return 0;
        }
        //实际创建一个渲染程序（program）
        int program = GLES20.glCreateProgram();
        if (program == 0) {
            return 0;
        }
        //将着色器程序添加到渲染程序中
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, segmentShader);
        //链接源程序
        GLES20.glLinkProgram(program);
        int[] params = new int[1];
        GLES20.glGetProgramiv(program,
                GLES20.GL_LINK_STATUS, params, 0);
        if (params[0] != GLES20.GL_TRUE) {
            GLES20.glDeleteProgram(program);
            program = 0;
        }
        return program;
    }

    /**
     * 加载shader源码并编译shader
     *
     * @param shaderType
     * @param source
     * @return
     */
    public static int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);
        int[] params = new int[1];
        GLES20.glGetShaderiv(shader,
                GLES20.GL_COMPILE_STATUS, params, 0);
        if (params[0] != GLES20.GL_TRUE) {
            GLES20.glDeleteShader(shader);
            shader = 0;
        }
        return shader;
    }
}
