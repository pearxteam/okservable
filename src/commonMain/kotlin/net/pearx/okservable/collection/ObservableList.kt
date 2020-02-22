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
import net.pearx.okservable.internal.removeBulkRandomAccess
import net.pearx.okservable.internal.subListBy

open class ObservableListSimple<C : MutableList<E>, E>(base: C, onUpdate: ObservableHandlerSimple) : ObservableCollectionSimple<C, E>(base, onUpdate), MutableList<E> {
    override fun add(index: Int, element: E) = base.add(index, element).also { onUpdate() }

    override fun addAll(index: Int, elements: Collection<E>): Boolean = base.addAll(index, elements).ifTrue(onUpdate)

    override fun listIterator(): MutableListIterator<E> = ObservableMutableListIteratorSimple(base.listIterator(), onUpdate)

    override fun listIterator(index: Int): MutableListIterator<E> = ObservableMutableListIteratorSimple(base.listIterator(index), onUpdate)

    override fun removeAt(index: Int): E = base.removeAt(index).also { onUpdate() }

    override fun set(index: Int, element: E): E = base.set(index, element).also { if (element != it) onUpdate() }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> = subListBy(this, fromIndex, toIndex)

    override fun get(index: Int): E = base[index]

    override fun indexOf(element: E): Int = base.indexOf(element)

    override fun lastIndexOf(element: E): Int = base.lastIndexOf(element)
}

open class ObservableListSimpleRA<C : MutableList<E>, E>(base: C, onUpdate: ObservableHandlerSimple) : ObservableListSimple<C, E>(base, onUpdate), RandomAccess


abstract class AbstractObservableList<C : MutableList<E>, E>(base: C, onUpdate: ObservableListHandler<E>) : AbstractObservableCollection<C, E, ObservableListHandler<E>>(base, onUpdate), MutableList<E> {
    override fun add(element: E): Boolean = add(size, element).let { true }

    override fun add(index: Int, element: E) = base.add(index, element).also { onUpdate.onAdd(index, element) }

    override fun addAll(elements: Collection<E>): Boolean = addAll(size, elements)

    override fun addAll(index: Int, elements: Collection<E>): Boolean = base.addAll(index, elements).ifTrue { elements.forEach { onUpdate.onAdd(index, it) } }

    override fun iterator(): MutableIterator<E> = listIterator()

    abstract override fun remove(element: E): Boolean

    abstract override fun removeAll(elements: Collection<E>): Boolean

    abstract override fun retainAll(elements: Collection<E>): Boolean

    override fun listIterator(): MutableListIterator<E> = listIterator(0)

    override fun listIterator(index: Int): MutableListIterator<E> = ObservableMutableListIterator(base.listIterator(index), onUpdate)

    override fun removeAt(index: Int): E = base.removeAt(index).also { onUpdate.onRemove(index, it) }

    override fun set(index: Int, element: E): E = base.set(index, element).also { onUpdate.onSet(index, it, element) }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> = subListBy(this, fromIndex, toIndex)

    override fun get(index: Int): E = base[index]

    override fun indexOf(element: E): Int = base.indexOf(element)

    override fun lastIndexOf(element: E): Int = base.lastIndexOf(element)
}

open class ObservableList<C : MutableList<E>, E>(base: C, onUpdate: ObservableListHandler<E>) : AbstractObservableList<C, E>(base, onUpdate) {
    override fun removeAll(elements: Collection<E>): Boolean = removeBulk(elements, true)

    override fun retainAll(elements: Collection<E>): Boolean = removeBulk(elements, false)

    override fun remove(element: E): Boolean {
        val it = iterator()
        for (el in it)
            if (el == element) {
                it.remove()
                return true
            }
        return false
    }
}

open class ObservableListRA<C : MutableList<E>, E>(base: C, onUpdate: ObservableListHandler<E>) : AbstractObservableList<C, E>(base, onUpdate), RandomAccess {
    override fun removeAll(elements: Collection<E>): Boolean = removeBulkRandomAccess(elements, true)

    override fun retainAll(elements: Collection<E>): Boolean = removeBulkRandomAccess(elements, false)

    override fun remove(element: E): Boolean {
        for(i in 0 until size) {
            val el = get(i)
            if(el == element) {
                removeAt(i)
                return true
            }
        }
        return false
    }
}


fun <C : MutableList<E>, E> C.observableListSimple(onUpdate: ObservableHandlerSimple): MutableList<E> = if (this is RandomAccess) ObservableListSimpleRA(this, onUpdate) else ObservableListSimple(this, onUpdate)
fun <C : MutableList<E>, E> C.observableList(onUpdate: ObservableListHandler<E>): MutableList<E> = if (this is RandomAccess) ObservableListRA(this, onUpdate) else ObservableList(this, onUpdate)
inline fun <C : MutableList<E>, E> C.observableList(crossinline block: ObservableListHandlerScope<E>.() -> Unit): MutableList<E> = observableList(ObservableListHandlerScope<E>().also(block).createHandler())