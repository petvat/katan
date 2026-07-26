package io.github.petvat.katan.ui.viewmodel

import io.github.petvat.katan.event.EventListener
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class ViewModel() : EventListener {

    // abstract fun onCommand(cmd: Command)

    @PublishedApi
    internal val actionsMap = mutableMapOf<KProperty<*>, MutableList<(Any) -> Unit>>()

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> onPropertyChange(property: KProperty<T>, noinline action: (T) -> Unit) {
        val actions = actionsMap.getOrPut(property) { mutableListOf() } as MutableList<(T) -> Unit>
        actions += action
    }

    inline fun <reified T> propertyNotify(initialValue: T): ReadWriteProperty<ViewModel, T> =
        Delegates.vetoable(initialValue) { property, oldValue, newValue ->
            if (newValue != oldValue) notify(property, newValue as Any)
            true
        }

    fun notify(property: KProperty<*>, value: Any?) {
        actionsMap[property]?.forEach { action ->
            if (value != null) {
                action(value)
            }
        }
    }


}
