//用于在java代码中获取的属性
attribute vec4 position;
void main(){
    //gl_Position是内置变量，opengl绘制顶点就是根据它的值绘制的，
    //所以我们需要把我们自己的值赋值给它。
    gl_Position = position;
}