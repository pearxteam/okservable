/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

@file:Suppress("UNCHECKED_CAST")

package net.pearx.okservable.collection.iterator

import net.pearx.okservable.collection.ObservableCollectionEvent
import net.pearx.okservable.collection.ObservableCollectionHandler
import net.pearx.okservable.collection.ObservableCollectionScope


class ObservableMutableIterator<T>(private val base: MutableIterator<T>, private val onUpdate: ObservableCollectionHandler<T>) : MutableIterator<T> by base {
    private var lastElement: T? = null

    override fun next(): T = base.next().also { lastElement = it }

    override fun remove() {
        base.remove()
        onUpdate(ObservableCollectionScope(ObservableCollectionEvent.ElementRemoved(lastElement as T)))
    }
}