/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.okservable.collection

import net.pearx.okservable.collection.iterator.ObservableMutableIterator
import net.pearx.okservable.internal.ifTrue
import net.pearx.okservable.internal.removeBulk

inline class ObservableCollectionScope<out T>(val event: ObservableCollectionEvent<T>)

typealias ObservableCollectionHandler<T> = ObservableCollectionScope<T>.() -> Unit

internal fun <T> ObservableCollectionHandler<T>.send(event: ObservableCollectionEvent<T>) = this(ObservableCollectionScope(event))

sealed class ObservableCollectionEvent<out T> {
    class PreClear<out T>(val elements: Collection<T>) : ObservableCollectionEvent<T>()
    class PostClear<out T> : ObservableCollectionEvent<T>()
    class ElementAdded<out T>(val element: T) : ObservableCollectionEvent<T>()
    class ElementRemoved<out T>(val element: T) : ObservableCollectionEvent<T>()
}

inline fun <T> ObservableCollectionScope<T>.preClear(block: (elements: Collection<T>) -> Unit) {
    val event = event
    if (event is ObservableCollectionEvent.PreClear) block(event.elements)
}

inline fun <T> ObservableCollectionScope<T>.postClear(block: () -> Unit) {
    if (event is ObservableCollectionEvent.PostClear) block()
}

inline fun <T> ObservableCollectionScope<T>.add(block: (element: T) -> Unit) {
    val event = event
    if (event is ObservableCollectionEvent.ElementAdded) block(event.element)
}

inline fun <T> ObservableCollectionScope<T>.remove(block: (element: T) -> Unit) {
    val event = event
    if (event is ObservableCollectionEvent.ElementRemoved) block(event.element)
}

open class ObservableCollection<C : MutableCollection<E>, E>(protected val base: C, protected val onUpdate: ObservableCollectionHandler<E>) : MutableCollection<E> by base {
    override fun clear() {
        if(size > 0) {
            onUpdate.send(ObservableCollectionEvent.PreClear(this))
            base.clear()
            onUpdate.send(ObservableCollectionEvent.PostClear())
        }
    }

    override fun add(element: E): Boolean = base.add(element).ifTrue { onUpdate.send(ObservableCollectionEvent.ElementAdded(element)) }

    override fun addAll(elements: Collection<E>): Boolean {
        var modified = false
        for (el in elements) {
            if (add(el))
                modified = true
        }
        return modified
    }

    override fun iterator(): MutableIterator<E> = ObservableMutableIterator(base.iterator(), onUpdate)

    override fun remove(element: E): Boolean = base.remove(element).ifTrue { onUpdate.send(ObservableCollectionEvent.ElementRemoved(element)) }

    override fun removeAll(elements: Collection<E>): Boolean = removeBulk(elements, true)

    override fun retainAll(elements: Collection<E>): Boolean = removeBulk(elements, false)

    override fun equals(other: Any?): Boolean = base == other

    override fun hashCode(): Int = base.hashCode()

    override fun toString(): String = base.toString()
}


fun <C : MutableCollection<E>, E> C.observableCollection(onUpdate: ObservableCollectionHandler<E>): MutableCollection<E> = ObservableCollection(this, onUpdate)