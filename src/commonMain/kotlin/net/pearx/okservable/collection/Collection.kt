/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.okservable.collection

import net.pearx.okservable.collection.iterator.ObservableMutableIterator
import net.pearx.okservable.collection.iterator.ObservableMutableIteratorSimple
import net.pearx.okservable.internal.ifTrue
import net.pearx.okservable.internal.removeBulk

interface IObservableCollection<C : MutableCollection<E>, E> : MutableCollection<E> {
    override val size: Int
        get() = base.size

    override fun contains(element: E): Boolean = element in base

    override fun containsAll(elements: Collection<E>): Boolean = base.containsAll(elements)

    override fun isEmpty(): Boolean = base.isEmpty()

    val base: C
}


abstract class AbstractObservableCollectionSimple<C : MutableCollection<E>, E>(override val base: C, protected val onUpdate: ObservableCollectionHandlerSimple) : IObservableCollection<C, E> {
    override fun add(element: E): Boolean = base.add(element).ifTrue(onUpdate)

    override fun addAll(elements: Collection<E>): Boolean = base.addAll(elements).ifTrue(onUpdate)

    override fun clear() {
        val previousSize = base.size
        base.clear()
        if (previousSize != base.size)
            onUpdate()
    }

    override fun iterator(): MutableIterator<E> = ObservableMutableIteratorSimple(base.iterator(), onUpdate)

    override fun remove(element: E): Boolean = base.remove(element).ifTrue(onUpdate)

    override fun removeAll(elements: Collection<E>): Boolean = base.removeAll(elements).ifTrue(onUpdate)

    override fun retainAll(elements: Collection<E>): Boolean = base.retainAll(elements).ifTrue(onUpdate)

    override fun equals(other: Any?): Boolean = base == other

    override fun hashCode(): Int = base.hashCode()

    override fun toString(): String = base.toString()
}
open class ObservableCollectionSimple<C : MutableCollection<E>, E>(base: C, onUpdate: ObservableCollectionHandlerSimple) : AbstractObservableCollectionSimple<C, E>(base, onUpdate)
open class ObservableCollectionSimpleRA<C : MutableCollection<E>, E>(base: C, onUpdate: ObservableCollectionHandlerSimple) : ObservableCollectionSimple<C, E>(base, onUpdate), RandomAccess


abstract class AbstractObservableCollection<C : MutableCollection<E>, E, U : AbstractObservableCollectionHandler<E>>(override val base: C, protected val onUpdate: U) : IObservableCollection<C, E> {
    override fun clear() {
        if(size > 0) {
            val lst = ArrayList(this)
            base.clear()
            onUpdate.onClear(lst)
        }
    }

    override fun equals(other: Any?): Boolean = base == other

    override fun hashCode(): Int = base.hashCode()

    override fun toString(): String = base.toString()
}
open class ObservableCollection<C : MutableCollection<E>, E>(base: C, onUpdate: ObservableCollectionHandler<E>) : AbstractObservableCollection<C, E, ObservableCollectionHandler<E>>(base, onUpdate) {
    override fun add(element: E): Boolean = base.add(element).ifTrue { onUpdate.onAdd(element) }

    override fun addAll(elements: Collection<E>): Boolean {
        var modified = false
        for (el in elements) {
            if (add(el))
                modified = true
        }
        return modified
    }

    override fun iterator(): MutableIterator<E> = ObservableMutableIterator(base.iterator(), onUpdate)

    override fun remove(element: E): Boolean = base.remove(element).ifTrue { onUpdate.onRemove(element) }

    override fun removeAll(elements: Collection<E>): Boolean = removeBulk(elements, true)

    override fun retainAll(elements: Collection<E>): Boolean = removeBulk(elements, false)


}
open class ObservableCollectionRA<C : MutableCollection<E>, E>(base: C, onUpdate: ObservableCollectionHandler<E>) : ObservableCollection<C, E>(base, onUpdate), RandomAccess


fun <C : MutableCollection<E>, E> C.observableSimple(onUpdate: ObservableCollectionHandlerSimple): IObservableCollection<C, E> = if (this is RandomAccess) ObservableCollectionSimpleRA(this, onUpdate) else ObservableCollectionSimple(this, onUpdate)
fun <C : MutableCollection<E>, E> C.observable(onUpdate: ObservableCollectionHandler<E>): IObservableCollection<C, E> = if (this is RandomAccess) ObservableCollectionRA(this, onUpdate) else ObservableCollection(this, onUpdate)
inline fun <C : MutableCollection<E>, E> C.observable(crossinline block: ObservableCollectionHandlerScope<E>.() -> Unit): IObservableCollection<C, E> = observable(ObservableCollectionHandlerScope<E>().also(block).createHandler())
//fun <C : MutableCollection<E>, E> observableCollectionSimpleBy(base: C, onUpdate: ObservableCollectionHandlerSimple): IObservableCollection<C, E> = if (base is RandomAccess) ObservableCollectionSimpleRA(base, onUpdate) else ObservableCollectionSimple(base, onUpdate)
//fun <C : MutableCollection<E>, E> observableCollectionBy(base: C, onUpdate: ObservableCollectionHandler<E>): IObservableCollection<C, E> = if (base is RandomAccess) ObservableCollectionRA(base, onUpdate) else ObservableCollection(base, onUpdate)
//inline fun <C : MutableCollection<E>, E> observableCollectionBy(base: C, crossinline block: ObservableCollectionHandlerScope<E>.() -> Unit): IObservableCollection<C, E> = observableCollectionBy(base, ObservableCollectionHandlerScope<E>().also(block).createHandler())