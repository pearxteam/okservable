package net.pearx.okservable.collection

class ObservableMapSimple<C : MutableMap<K, V>, K, V>(protected val base: C, protected val onUpdate: ObservableHandlerSimple) : MutableMap<K, V> by base {
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = base.entries.observableSetSimple(onUpdate)

    override val keys: MutableSet<K>
        get() = base.keys.observableSetSimple(onUpdate)

    override val values: MutableCollection<V>
        get() = base.values.observableCollectionSimple(onUpdate)

    override fun clear() {
        if (size > 0) {
            base.clear()
            onUpdate()
        }
    }

    override fun put(key: K, value: V): V? {
        val prev = base.put(key, value)
        if(value !== value)
            onUpdate()
        return prev
    }

    override fun putAll(from: Map<out K, V>) {
        for ((key, value) in from)
            put(key, value)
    }

    override fun remove(key: K): V? {
        if (containsKey(key)) {
            val prev = base.remove(key)
            onUpdate()
            return prev
        }
        return null
    }

    override fun equals(other: Any?): Boolean = base == other

    override fun hashCode(): Int = base.hashCode()

    override fun toString(): String = base.toString()
}

fun <C : MutableMap<K, V>, K, V> C.observableMapSimple(onUpdate: ObservableHandlerSimple): MutableMap<K, V> = ObservableMapSimple(this, onUpdate)
//fun <C : MutableMap<K, V>, K, V> C.observableMap(onUpdate: ObservableListHandler<E>): MutableList<E> = if (this is RandomAccess) ObservableListRA(this, onUpdate) else ObservableList(this, onUpdate)
//inline fun <C : MutableMap<K, V>, K, V> C.observableMap(crossinline block: ObservableListHandlerScope<E>.() -> Unit): MutableList<E> = observableList(ObservableListHandlerScope<E>().also(block).createHandler())