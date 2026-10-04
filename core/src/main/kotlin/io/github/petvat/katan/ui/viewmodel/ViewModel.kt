package io.github.petvat.katan.ui.viewmodel

import io.github.petvat.katan.event.Event
import io.github.petvat.katan.event.EventListener
import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


abstract class PropertyNotifiable {
    @PublishedApi
    internal val actionsMap = mutableMapOf<KProperty<*>, MutableList<(Any?) -> Unit>>()

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T> onPropertyChange(property: KProperty<T>, noinline action: (T) -> Unit) {
        val actions = actionsMap.getOrPut(property) { mutableListOf() } as MutableList<(T) -> Unit>
        actions += action
    }

    inline fun <reified T> propertyNotify(initialValue: T): ReadWriteProperty<PropertyNotifiable, T> =
        Delegates.vetoable(initialValue) { property, oldValue, newValue ->
            if (newValue != oldValue) notify(property, newValue)
            true
        }

    fun notify(property: KProperty<*>, value: Any?) {
        actionsMap[property]?.forEach { action -> action(value) }
    }


    inner class StateMirror<T : Any, R>(
        private val source: () -> T?,
        initial: T,
        private val project: (T) -> R,
        private val gate: () -> Boolean = { true },
    ) : ReadOnlyProperty<PropertyNotifiable, R> {
        private var snapshot: T = initial
        private var projected: R = project(initial)
        private var key: KProperty<*>? = null

        override fun getValue(
            thisRef: PropertyNotifiable,
            property: KProperty<*>
        ): R {
            if (key == null) key = property
            return projected
        }

        fun poll() {
            if (!gate()) return // frozen: keep serving the last projection
            val latest = source() ?: return
            if (latest == snapshot) return
            projected = project(latest)
            key?.let { notify(it, projected) }
        }
    }

    protected val mirrors = mutableListOf<StateMirror<*, *>>()

    fun <T : Any, R> mirror(
        source: () -> T?,
        initial: T,
        project: (T) -> R,
        gate: () -> Boolean = { true },
    ): StateMirror<T, R> = StateMirror(source, initial, project, gate).also { mirrors += it }

}


abstract class ViewModel : EventListener, PropertyNotifiable() {

    open fun refresh() {
        mirrors.forEach { it.poll() }
    }
}
