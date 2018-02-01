package com.sitexa.ktor

/**
 * Created by open on 21/04/2017.
 *
 */

fun main(vararg: Array<String>) {
    println("Hello world!")
    xyz()
}

fun xyz() {
    val num = 1000
    val result = 4f

    for (x in 1..num) {
        for (y in 1..num) {
            for (z in 1..num) {
                val a = x.toFloat() / (y.toFloat() + z.toFloat())
                val b = y.toFloat() / (z.toFloat() + x.toFloat())
                val c = z.toFloat() / (x.toFloat() + y.toFloat())

                val r = a + b + c

                if (result == r) {
                    println("x=$x;y=$y;z=$z")
                    println("a=$a;b=$b;c=$c")
                    println("===============")
                }
            }
        }
    }
}