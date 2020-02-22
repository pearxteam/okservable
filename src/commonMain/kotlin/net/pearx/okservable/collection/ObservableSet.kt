/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.okservable.collection


open class ObservableSetSimple<C : MutableSet<E>, E>(base: C, onUpdate: ObservableHandlerSimple) : ObservableCollectionSimple<C, E>(base, onUpdate), MutableSet<E>
open class ObservableSetSimpleRA<C : MutableSet<E>, E>(base: C, onUpdate: ObservableHandlerSimple) : ObservableSetSimple<C, E>(base, onUpdate), RandomAccess


open class ObservableSet<C : MutableSet<E>, E>(base: C, onUpdate: ObservableCollectionHandler<E>) : ObservableCollection<C, E>(base, onUpdate), MutableSet<E>
open class ObservableSetRA<C : MutableSet<E>, E>(base: C, onUpdate: ObservableCollectionHandler<E>) : ObservableSet<C, E>(base, onUpdate), RandomAccess


fun <C : MutableSet<E>, E> C.observableSetSimple(onUpdate: ObservableHandlerSimple): MutableSet<E> = if (this is RandomAccess) ObservableSetSimpleRA(this, onUpdate) else ObservableSetSimple(this, onUpdate)
fun <C : MutableSet<E>, E> C.observableSet(onUpdate: ObservableCollectionHandler<E>): MutableSet<E> = if (this is RandomAccess) ObservableSetRA(this, onUpdate) else ObservableSet(this, onUpdate)
inline fun <C : MutableSet<E>, E> C.observableSet(crossinline block: ObservableCollectionHandlerScope<E>.() -> Unit): MutableSet<E> = observableSet(ObservableCollectionHandlerScope<E>().also(block).createHandler())