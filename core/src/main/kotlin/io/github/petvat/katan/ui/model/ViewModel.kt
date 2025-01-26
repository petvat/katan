package io.github.petvat.katan.ui.model

import io.github.petvat.katan.event.EventListener
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


// TODO: Shared behavior
abstract class ViewModel() : EventListener {


    @PublishedApi
    internal val actionsMap = mutableMapOf<KProperty<*>, MutableList<(Any) -> Unit>>()


    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> onPropertyChange(property: KProperty<T>, noinline action: (T) -> Unit) {
        val actions = actionsMap.getOrPut(property) { mutableListOf() } as MutableList<(T) -> Unit>
    }


    inline fun <reified T : Any> propertyNotify(initialValue: T): ReadWriteProperty<ViewModel, T> =
        Delegates.observable(initialValue) { property, _, newValue -> notify(property, newValue) }

    fun notify(property: KProperty<*>, value: Any) {
        actionsMap[property]?.forEach { action -> action(value) }
    }


}
