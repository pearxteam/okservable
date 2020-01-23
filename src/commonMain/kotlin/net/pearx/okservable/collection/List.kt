/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.okservable.collection

import net.pearx.okservable.collection.iterator.ObservableMutableListIterator
import net.pearx.okservable.collection.iterator.ObservableMutableListIteratorSimple
import net.pearx.okservable.internal.ifTrue
import net.pearx.okservable.internal.removeBulk
import net.pearx.okservable.internal.subListBy

interface IObservableList<C : MutableList<E>, E> : IObservableCollection<C, E>, MutableList<E> {
    override val size: Int
        get() = super.size

    override fun containsAll(elements: Collection<E>): Boolean = super.containsAll(elements)

    override fun contains(element: E): Boolean = super.contains(element)

    override fun isEmpty(): Boolean = super.isEmpty()

    override fun get(index: Int): E = base[index]

    override fun indexOf(element: E): Int = base.indexOf(element)

    override fun lastIndexOf(element: E): Int = base.lastIndexOf(element)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> = subListBy(this, fromIndex, toIndex)
}


open class ObservableListSimple<C : MutableList<E>, E>(base: C, onUpdate: ObservableListHandlerSimple) : AbstractObservableCollectionSimple<C, E>(base, onUpdate), IObservableList<C, E> {
    override fun add(index: Int, element: E) = base.add(index, element).also { onUpdate() }

    override fun addAll(index: Int, elements: Collection<E>): Boolean = base.addAll(index, elements).ifTrue(onUpdate)

    override fun listIterator(): MutableListIterator<E> = ObservableMutableListIteratorSimple(base.listIterator(), onUpdate)

    override fun listIterator(index: Int): MutableListIterator<E> = ObservableMutableListIteratorSimple(base.listIterator(index), onUpdate)

    override fun removeAt(index: Int): E = base.removeAt(index).also { onUpdate() }

    override fun set(index: Int, element: E): E = base.set(index, element).also { if (element != it) onUpdate() }
}
open class ObservableListSimpleRA<C : MutableList<E>, E>(base: C, onUpdate: ObservableCollectionHandlerSimple) : ObservableListSimple<C, E>(base, onUpdate), RandomAccess


open class ObservableList<C : MutableList<E>, E>(base: C, onUpdate: ObservableListHandler<E>) : AbstractObservableCollection<C, E, ObservableListHandler<E>>(base, onUpdate), IObservableList<C, E> {
    override fun add(element: E): Boolean = add(size, element).let { true }

    override fun add(index: Int, element: E) = base.add(index, element).also { onUpdate.onAdd(index, element) }

    override fun addAll(elements: Collection<E>): Boolean = addAll(size, elements)

    override fun addAll(index: Int, elements: Collection<E>): Boolean = base.addAll(index, elements).ifTrue { elements.forEach { onUpdate.onAdd(index, it) } }

    override fun iterator(): MutableIterator<E> = listIterator()

    override fun remove(element: E): Boolean {
        val it = iterator()
        for (el in it)
            if (el == element) {
                it.remove()
                return true
            }
        return false
    }

    override fun removeAll(elements: Collection<E>): Boolean = removeBulk(elements, true)

    override fun retainAll(elements: Collection<E>): Boolean = removeBulk(elements, false)

    override fun listIterator(): MutableListIterator<E> = listIterator(0)

    override fun listIterator(index: Int): MutableListIterator<E> = ObservableMutableListIterator(base.listIterator(index), onUpdate)

    override fun removeAt(index: Int): E = base.removeAt(index).also { onUpdate.onRemove(index, it) }

    override fun set(index: Int, element: E): E = base.set(index, element).also { onUpdate.onSet(index, it, element) }

}

open class ObservableListRA<C : MutableList<E>, E>(base: C, onUpdate: ObservableListHandler<E>) : ObservableList<C, E>(base, onUpdate), RandomAccess


fun <C : MutableList<E>, E> C.observableSimple(onUpdate: ObservableListHandlerSimple): IObservableList<C, E> = if (this is RandomAccess) ObservableListSimpleRA(this, onUpdate) else ObservableListSimple(this, onUpdate)
fun <C : MutableList<E>, E> C.observable(onUpdate: ObservableListHandler<E>): IObservableList<C, E> = if (this is RandomAccess) ObservableListRA(this, onUpdate) else ObservableList(this, onUpdate)
inline fun <C : MutableList<E>, E> C.observable(crossinline block: ObservableListHandlerScope<E>.() -> Unit): IObservableList<C, E> = observable(ObservableListHandlerScope<E>().also(block).createHandler())
//fun <C : MutableList<E>, E> observableListSimpleBy(base: C, onUpdate: ObservableListHandlerSimple): IObservableList<C, E> = if(base is RandomAccess) ObservableListSimpleRA(base, onUpdate) else ObservableListSimple(base, onUpdate)
//fun <C : MutableList<E>, E> observableListBy(base: C, onUpdate: ObservableListHandler<E>): IObservableList<C, E> = if(base is RandomAccess) ObservableListRA(base, onUpdate) else ObservableList(base, onUpdate)
//inline fun <C : MutableList<E>, E> observableListBy(base: C, crossinline block: ObservableListHandlerScope<E>.() -> Unit): IObservableList<C, E> = observableListBy(base, ObservableListHandlerScope<E>().also(block).createHandler())