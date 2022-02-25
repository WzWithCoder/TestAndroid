package com.example.kotlin

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_test_layout.*
import java.util.*
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.system.measureTimeMillis

//https://mbd.baidu.com/ug_share/mbox/4a81af9963/share?tk=56b4cd6858cf9b5b73b4842a60365661&share_url=https%3A%2F%2Fzoyi14.smartapps.cn%2Fpages%2Fnote%2Findex%3Fslug%3Df5f0d38e3e44%26origin%3Dshare%26_swebfr%3D1%26_swebFromHost%3Dbaiduboxapp

//方法扩展
fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_layout)

        applicationContext.toast("xx")

        container.setOnClickListener({

        })
        container.setOnTouchListener{v ,e->

            return@setOnTouchListener false
        }
    }
}

var param: String by Delegate()
val lazyParam: String by lazy {
    println("computed!")
    "lazyValue"
}
var observeParam: String by Delegates.observable("observeValue") { prop, old, new ->
    println("$prop -> $old -> $new")
}

typealias MyHandler = (Int, String, Any) -> Unit


suspend fun main(args: Array<String>) {
    //集合Stream
    val a = arrayOf("orange", "xx")
    val fruits = asList("apple", "banana", "kiwifruit", *a)
    fruits.filter { it.startsWith("a") }
            .sortedBy { it }
            .map { it.toUpperCase() }
            .forEach { println(it) }

    val point = object {
        var x: Int = 10
        var y: Int = 5
    }
    println(point.x + point.y)

    val user = User(mapOf(
            "name" to "John Doe",
            "age" to 25
    ))
    println(user.name + user.age)

    foo(10, 5, {
        println("函数回调:" + it)
    })

    //中缀表达式
    println(2 on 1)

    var str = fruits.fold("cc", { acc: String, i: String ->
        print("acc = $acc, i = $i, ")
        val result = acc + i
        println("result = $result")
        // lambda 表达式中的最后一个表达式是返回值：
        result
    })
    println(str)

    //测量时长
    measureTimeMillis {
        //...
    }

    //函数类型接口
    class IntTransformer : (Int, String) -> String {
        override fun invoke(p1: Int, p2: String): String {
            return p2 + ":" + p1
        }
    }
    val initFunction: (Int, String) -> String = IntTransformer()
    println(initFunction(1, "函数类型接口"))
}

fun <T, R> Collection<T>.fold(
        initial: R,
        combine: (acc: R, nextElement: T) -> R
): R {
    var accumulator: R = initial
    for (element: T in this) {
        accumulator = combine(accumulator, element)
    }
    return accumulator
}

infix fun <T> T.on(t: T): Boolean {
    return this == t
}

fun <T> asList(vararg ts: T): List<T> {
    val result = ArrayList<T>()
    for (t in ts) // ts is an Array
        result.add(t)
    return result
}

fun foo(bar: Int = 0,
        baz: Int = 1,
        qux: (Int) -> Unit) {
    qux(bar + baz)
}

class User(val map: Map<String, Any?>) {
    val name: String by map
    val age: Int     by map
}


class Delegate<T> : ReadWriteProperty<Any?, T> {
    private var value: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value ?: throw IllegalStateException("Property " +
                "${property.name} should be initialized before get.")
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}



