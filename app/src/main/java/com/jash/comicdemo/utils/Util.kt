package com.jash.comicdemo.utils

fun Any.update(a: Any?) {
    if (a != null && this.javaClass.isInstance(a)) {
        val fields = this.javaClass.declaredFields
        fields.forEach {
            it.isAccessible = true
            if (it.get(this) == null) {
                it.set(this, it.get(a))
            }
        }
    }
}
