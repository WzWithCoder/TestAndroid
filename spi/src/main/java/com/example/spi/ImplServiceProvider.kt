package com.example.spi

/**
 * Create by wangzheng on 2022/1/7
 */
class ImplServiceProvider : IServiceProvider {
    override fun invoke(vararg params: Any) {
        println(params[0].toString())
    }
}
