package com.indoorway.coffeehunt.common

fun <T> concat(one: Iterable<T>, two: Iterable<T>): Iterable<T> {
    return object : Iterable<T> {
        override fun iterator(): Iterator<T> {
            return object : Iterator<T> {
                val oneI = one.iterator()
                val twoI = two.iterator()

                override fun hasNext() =
                        oneI.hasNext() || twoI.hasNext()

                override fun next() =
                        if (oneI.hasNext()) {
                            oneI.next()
                        } else {
                            twoI.next()
                        }
            }
        }
    }
}