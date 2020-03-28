/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.okservable.collection

import net.pearx.okservable.collection.iterator.ObservableMutableListIterator
import net.pearx.okservable.internal.*
import net.pearx.okservable.internal.ifTrue
import net.pearx.okservable.internal.removeBulk
import net.pearx.okservable.internal.removeBulkRandomAccess
import net.pearx.okservable.internal.subListBy

inline class ObservableListScope<out T>(val event: ObservableListEvent<T>)

typealias ObservableListHandler<T> = ObservableListScope<T>.() -> Unit

internal fun <T> ObservableListHandler<T>.send(event: ObservableListEvent<T>) = this(ObservableListScope(event))

sealed class ObservableListEvent<out T> {
    class PreClear<out T> : ObservableListEvent<T>()
    class PostClear<out T> : ObservableListEvent<T>()
    class ElementAdded<out T>(val index: Int, val element: T) : ObservableListEvent<T>()
    class ElementRemoved<out T>(val index: Int, val element: T) : ObservableListEvent<T>()
    class ElementSet<out T>(val index: Int, val prevElement: T, val newElement: T) : ObservableListEvent<T>()
}

inline fun <T> ObservableListScope<T>.preClear(block: () -> Unit) {
    if (event is ObservableListEvent.PreClear) block()
}

inline fun <T> ObservableListScope<T>.postClear(block: () -> Unit) {
    if (event is ObservableListEvent.PostClear) block()
}

inline fun <T> ObservableListScope<T>.add(block: (index: Int, element: T) -> Unit) {
    val event = event
    if (event is ObservableListEvent.ElementAdded) block(event.index, event.element)
}

inline fun <T> ObservableListScope<T>.remove(block: (index: Int, element: T) -> Unit) {
    val event = event
    if (event is ObservableListEvent.ElementRemoved) block(event.index, event.element)
}

inline fun <T> ObservableListScope<T>.set(block: (index: Int, prevElement: T, newElement: T) -> Unit) {
    val event = event
    if (event is ObservableListEvent.ElementSet) block(event.index, event.prevElement, event.newElement)
}

abstract class AbstractObservableList<C : MutableList<E>, E>(protected val base: C, protected val onUpdate: ObservableListHandler<E>) : MutableList<E> by base {
    override fun add(element: E): Boolean = add(size, element).let { true }

    override fun add(index: Int, element: E) = base.add(index, element).also { onUpdate.send(ObservableListEvent.ElementAdded(index, element)) }

    override fun addAll(elements: Collection<E>): Boolean = addAll(size, elements)

    override fun addAll(index: Int, elements: Collection<E>): Boolean = base.addAll(index, elements).ifTrue { elements.forEachIndexed { i, element -> onUpdate.send(ObservableListEvent.ElementAdded(index + i, element)) } }

    override fun iterator(): MutableIterator<E> = listIterator()

    abstract override fun remove(element: E): Boolean

    abstract override fun removeAll(elements: Collection<E>): Boolean

    abstract override fun retainAll(elements: Collection<E>): Boolean

    override fun listIterator(): MutableListIterator<E> = listIterator(0)

    override fun listIterator(index: Int): MutableListIterator<E> = ObservableMutableListIterator(base.listIterator(index), onUpdate)

    override fun removeAt(index: Int): E = base.removeAt(index).also { onUpdate.send(ObservableListEvent.ElementRemoved(index, it)) }

    override fun set(index: Int, element: E): E = base.set(index, element).also { onUpdate.send(ObservableListEvent.ElementSet(index, it, element)) }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> = subListBy(this, fromIndex, toIndex)

    override fun get(index: Int): E = base[index]

    override fun indexOf(element: E): Int = base.indexOf(element)

    override fun lastIndexOf(element: E): Int = base.lastIndexOf(element)

    override fun clear() {
        if(size > 0) {
            onUpdate.send(ObservableListEvent.PreClear())
            base.clear()
            onUpdate.send(ObservableListEvent.PostClear())
        }
    }

    override fun equals(other: Any?): Boolean = base == other

    override fun hashCode(): Int = base.hashCode()

    override fun toString(): String = base.toString()
}

open class ObservableList<C : MutableList<E>, E>(base: C, onUpdate: ObservableListHandler<E>) : AbstractObservableList<C, E>(base, onUpdate) {
    override fun removeAll(elements: Collection<E>): Boolean = removeBulk(elements, true)

    override fun retainAll(elements: Collection<E>): Boolean = removeBulk(elements, false)

    override fun remove(element: E): Boolean = removeSingle(element)
}

open class ObservableListRA<C : MutableList<E>, E>(base: C, onUpdate: ObservableListHandler<E>) : AbstractObservableList<C, E>(base, onUpdate), RandomAccess {
    override fun removeAll(elements: Collection<E>): Boolean = removeBulkRandomAccess(elements, true)

    override fun retainAll(elements: Collection<E>): Boolean = removeBulkRandomAccess(elements, false)

    override fun remove(element: E): Boolean = removeSingleRandomAccess(element)
}


fun <C : MutableList<E>, E> C.observableList(onUpdate: ObservableListHandler<E>): MutableList<E> = if (this is RandomAccess) ObservableListRA(this, onUpdate) else ObservableList(this, onUpdate)