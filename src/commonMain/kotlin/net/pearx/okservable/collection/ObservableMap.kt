/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

@file:Suppress("UNCHECKED_CAST")

package net.pearx.okservable.collection

import net.pearx.okservable.internal.removeBulk
import net.pearx.okservable.internal.removeSingle

open class ObservableMapSimple<C : MutableMap<K, V>, K, V>(protected val base: C, protected val onUpdate: ObservableHandlerSimple) : MutableMap<K, V> by base {
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
        if (prev !== value)
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

open class ObservableMap<C : MutableMap<K, V>, K, V>(protected val base: C, protected val onUpdate: ObservableMapHandler<K, V>) : MutableMap<K, V> by base {
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() {
            val baseEntries = base.entries
            return object : MutableSet<MutableMap.MutableEntry<K, V>> by baseEntries {
                override fun add(element: MutableMap.MutableEntry<K, V>): Boolean = throw UnsupportedOperationException()

                override fun addAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean = throw UnsupportedOperationException()

                override fun clear() {
                    this@ObservableMap.clear()
                }

                override fun iterator(): MutableIterator<MutableMap.MutableEntry<K, V>> {
                    val baseItr = baseEntries.iterator()
                    return object : MutableIterator<MutableMap.MutableEntry<K, V>> by baseItr {
                        private var lastElement: MutableMap.MutableEntry<K, V>? = null

                        override fun next(): MutableMap.MutableEntry<K, V> {
                            val next = baseItr.next()
                            lastElement = next
                            return object : MutableMap.MutableEntry<K, V> by next {
                                override fun setValue(newValue: V): V {
                                    val prev = next.setValue(newValue)
                                    if(prev !== newValue)
                                        onUpdate.onPut(key, prev, newValue)
                                    return prev
                                }
                            }
                        }

                        override fun remove() {
                            baseItr.remove()
                            onUpdate.onRemove(lastElement!!.key, lastElement!!.value)
                        }
                    }
                }

                override fun remove(element: MutableMap.MutableEntry<K, V>): Boolean {
                    if (containsKey(element.key)) {
                        forceRemove(element.key)
                        return true
                    }
                    return false
                }

                override fun removeAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean = removeBulk(elements, true)

                override fun retainAll(elements: Collection<MutableMap.MutableEntry<K, V>>): Boolean = removeBulk(elements, false)
            }
        }

    override val keys: MutableSet<K>
        get() {
            val baseEntries = base.entries
            val baseKeys = base.keys
            return object : MutableSet<K> by baseKeys {
                override fun add(element: K): Boolean = throw UnsupportedOperationException()

                override fun addAll(elements: Collection<K>): Boolean = throw UnsupportedOperationException()

                override fun clear() {
                    this@ObservableMap.clear()
                }

                override fun iterator(): MutableIterator<K> {
                    val baseItr = baseEntries.iterator()
                    return object : MutableIterator<K> {
                        private var lastElement: MutableMap.MutableEntry<K, V>? = null

                        override fun hasNext(): Boolean = baseItr.hasNext()

                        override fun next(): K {
                            val next = baseItr.next()
                            lastElement = next
                            return next.key
                        }

                        override fun remove() {
                            baseItr.remove()
                            onUpdate.onRemove(lastElement!!.key, lastElement!!.value)
                        }
                    }
                }

                override fun remove(element: K): Boolean {
                    if (containsKey(element)) {
                        forceRemove(element)
                        return true
                    }
                    return false
                }

                override fun removeAll(elements: Collection<K>): Boolean = removeBulk(elements, true)

                override fun retainAll(elements: Collection<K>): Boolean = removeBulk(elements, false)
            }
        }

    override val values: MutableCollection<V>
        get() {
            val baseEntries = base.entries
            val baseValues = base.values
            return object : MutableCollection<V> by baseValues {
                override fun add(element: V): Boolean = throw UnsupportedOperationException()

                override fun addAll(elements: Collection<V>): Boolean = throw UnsupportedOperationException()

                override fun clear() {
                    this@ObservableMap.clear()
                }

                override fun iterator(): MutableIterator<V> {
                    val baseItr = baseEntries.iterator()
                    return object : MutableIterator<V> {
                        private var lastElement: MutableMap.MutableEntry<K, V>? = null

                        override fun hasNext(): Boolean = baseItr.hasNext()

                        override fun next(): V {
                            val next = baseItr.next()
                            lastElement = next
                            return next.value
                        }

                        override fun remove() {
                            baseItr.remove()
                            onUpdate.onRemove(lastElement!!.key, lastElement!!.value)
                        }
                    }
                }

                override fun remove(element: V): Boolean = removeSingle(element)

                override fun removeAll(elements: Collection<V>): Boolean = removeBulk(elements, true)

                override fun retainAll(elements: Collection<V>): Boolean = removeBulk(elements, false)
            }
        }

    override fun clear() {
        if (size > 0) {
            val prev = HashMap(this)
            base.clear()
            onUpdate.onClear(prev)
        }
    }

    override fun put(key: K, value: V): V? {
        val prev = base.put(key, value)
        if (prev !== value)
            onUpdate.onPut(key, prev, value)
        return prev
    }

    override fun putAll(from: Map<out K, V>) {
        for ((key, value) in from)
            put(key, value)
    }

    override fun remove(key: K): V? {
        if (containsKey(key)) {
            forceRemove(key)
        }
        return null
    }



    private fun forceRemove(key: K): V {
        val prev = base.remove(key)
        onUpdate.onRemove(key, prev as V)
        return prev
    }

    override fun equals(other: Any?): Boolean = base == other

    override fun hashCode(): Int = base.hashCode()

    override fun toString(): String = base.toString()
}

fun <C : MutableMap<K, V>, K, V> C.observableMapSimple(onUpdate: ObservableHandlerSimple): MutableMap<K, V> = ObservableMapSimple(this, onUpdate)
fun <C : MutableMap<K, V>, K, V> C.observableMap(onUpdate: ObservableMapHandler<K, V>): MutableMap<K, V> = ObservableMap(this, onUpdate)
inline fun <C : MutableMap<K, V>, K, V> C.observableMap(crossinline block: ObservableMapHandlerScope<K, V>.() -> Unit): MutableMap<K, V> = observableMap(ObservableMapHandlerScope<K, V>().also(block).createHandler())