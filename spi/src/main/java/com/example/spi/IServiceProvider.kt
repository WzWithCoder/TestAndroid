package com.example.spi

interface IServiceProvider {
    fun invoke(vararg params: Any)
}