//声明用中等精度的float
precision mediump float;
//用于在java层传递颜色数据
uniform vec4 color;
void main(){
    //gl_FragColor内置变量，opengl渲染的颜色就是获取的它的值，
    //这里我们把我们自己的值赋值给它。
    gl_FragColor = color;
}