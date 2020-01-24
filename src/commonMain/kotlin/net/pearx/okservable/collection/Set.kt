/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.okservable.collection

interface IObservableSet<C : MutableSet<E>, E> : IObservableCollection<C, E>, MutableSet<E> {
    override val size: Int
        get() = super.size

    override fun contains(element: E): Boolean = super.contains(element)

    override fun containsAll(elements: Collection<E>): Boolean = super.containsAll(elements)

    override fun isEmpty(): Boolean = super.isEmpty()
}


open class ObservableSetSimple<C : MutableSet<E>, E>(base: C, onUpdate: ObservableCollectionHandlerSimple) : ObservableCollectionSimple<C, E>(base, onUpdate), IObservableSet<C, E>
open class ObservableSetSimpleRA<C : MutableSet<E>, E>(base: C, onUpdate: ObservableCollectionHandlerSimple) : ObservableSetSimple<C, E>(base, onUpdate), RandomAccess


open class ObservableSet<C : MutableSet<E>, E>(base: C, onUpdate: ObservableCollectionHandler<E>) : ObservableCollection<C, E>(base, onUpdate), IObservableSet<C, E>
open class ObservableSetRA<C : MutableSet<E>, E>(base: C, onUpdate: ObservableCollectionHandler<E>) : ObservableSet<C, E>(base, onUpdate), RandomAccess


fun <C : MutableSet<E>, E> C.observableSimple(onUpdate: ObservableCollectionHandlerSimple): IObservableSet<C, E> = if (this is RandomAccess) ObservableSetSimpleRA(this, onUpdate) else ObservableSetSimple(this, onUpdate)
fun <C : MutableSet<E>, E> C.observable(onUpdate: ObservableCollectionHandler<E>): IObservableSet<C, E> = if (this is RandomAccess) ObservableSetRA(this, onUpdate) else ObservableSet(this, onUpdate)
inline fun <C : MutableSet<E>, E> C.observable(crossinline block: ObservableCollectionHandlerScope<E>.() -> Unit): IObservableCollection<C, E> = observable(ObservableCollectionHandlerScope<E>().also(block).createHandler())
//fun <C : MutableSet<E>, E> observableSetSimpleBy(base: C, onUpdate: ObservableCollectionHandlerSimple): IObservableSet<C, E> = if(base is RandomAccess) ObservableSetSimpleRA(base, onUpdate) else ObservableSetSimple(base, onUpdate)
//fun <C : MutableSet<E>, E> observableSetBy(base: C, onUpdate: ObservableCollectionHandler<E>): IObservableSet<C, E> = if(base is RandomAccess) ObservableSetRA(base, onUpdate) else ObservableSet(base, onUpdate)
//inline fun <C : MutableSet<E>, E> observableSetBy(base: C, crossinline block: ObservableCollectionHandlerScope<E>.() -> Unit): IObservableSet<C, E> = observableSetBy(base, ObservableCollectionHandlerScope<E>().also(block).createHandler())