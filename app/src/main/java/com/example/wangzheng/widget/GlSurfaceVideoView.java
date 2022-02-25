package com.example.wangzheng.widget;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.example.wangzheng.R;
import com.example.wangzheng.common.ShaderKit;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Create by wangzheng on 2018/9/21
 */
public class GlSurfaceVideoView extends GLSurfaceView implements GLSurfaceView.Renderer {
    /*
     *OpenGL ES的顶点坐标系
     *
     * ******************
     * (-1,1)  *(0,1)   *
     *         *        *
     *    (0,0)*        *
     * * * * * * * * * **
     *(-1,0)   *   (1,0)*
     *         *        *
     *(-1,-1)  *(0,-1)  *
     * ******************
     * */
    private float[] vertexData = {
            -0.5f, -0.5f,//三角形左下角
            0.5f, -0.5f,//三角形右下角
            0.0f, 0.5f//三角形顶点
    };

    private FloatBuffer vertexBuffer;
    private int program;
    private int position;
    private int color;

    /**
     * Standard View constructor. In order to render something, you
     * must call {@link #setRenderer} to register a renderer.
     *
     * @param context
     */
    public GlSurfaceVideoView(Context context) {
        this(context, null);
    }

    /**
     * Standard View constructor. In order to render something, you
     * must call {@link #setRenderer} to register a renderer.
     *
     * @param context
     * @param attrs
     */
    public GlSurfaceVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //opengl-es 2.0
        setEGLContextClientVersion(2);
        //分配内存空间（单位字节）
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                //内存bit的排序方式和本地机器一致
                .order(ByteOrder.nativeOrder())
                //转换成float的buffer,因为我们是放float类型的顶点
                .asFloatBuffer()
                //把数据放入内存中
                .put(vertexData);
        //把索引指针指向开头位置
        vertexBuffer.position(0);

        setRenderer(this);
    }

    /**
     * Called when the surface is created or recreated.
     *
     * @param gl     the GL interface. Use <code>instanceof</code> to
     *               test if the interface supports GL11 or higher interfaces.
     * @param config the EGLConfig of the created surface. Can be used
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //加载shader
        String vertexSource = ShaderKit.id2content(getResources(), R.raw.vertex_shader);
        String segmentSource = ShaderKit.id2content(getResources(), R.raw.segment_shader);
        //创建渲染程序
        program = ShaderKit.createProgram(vertexSource, segmentSource);
        if (program > 0) {
            //获取颜色变量
            color = GLES20.glGetUniformLocation(program, "color");
            //获取顶点变量
            position = GLES20.glGetAttribLocation(program, "position");
        }
    }

    /**
     * Called when the surface changed size.
     *
     * @param gl     the GL interface. Use <code>instanceof</code> to
     *               test if the interface supports GL11 or higher interfaces.
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    /**
     * Called to draw the current frame.
     *
     * @param gl the GL interface. Use <code>instanceof</code> to
     *           test if the interface supports GL11 or higher interfaces.
     */
    @Override
    public void onDrawFrame(GL10 gl) {
        //清除颜色缓冲
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //清屏
        GLES20.glClearColor(0f, 0f, 0f, 0f);
        //使用着色器程序
        GLES20.glUseProgram(program);
        //分别设置片元变量的rgba四个值（glUniform4f:表示这是uniform类型的变量的4个float类型的值）
        GLES20.glUniform4f(color, 1f, 0f, 0f, 1f);
        //激活顶点属性
        GLES20.glEnableVertexAttribArray(position);
        //向顶点属性传递顶点数组的值
        //size--用的几个分量表示的一个点
        //normalized--是否做归一化处理
        //stride--每个点所占空间大小，因为是（x，y）2个点，每个点是4个字节，所以一个点占8个字节大小，
        GLES20.glVertexAttribPointer(position, 2,
                GLES20.GL_FLOAT, false, 8, vertexBuffer);
        //绘制这些顶点
        //mode--绘制的方式：GLES20.GL_TRIANGLES，单个三角形的方式
        //first--从哪个位置开始绘制，因为顶点坐标里面只有3个坐标点，所以从0开始绘制。
        //count--绘制多少个点，这里显然绘制三个点。
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }

}
