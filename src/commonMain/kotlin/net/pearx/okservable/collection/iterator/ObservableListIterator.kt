/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

@file:Suppress("UNCHECKED_CAST")

package net.pearx.okservable.collection.iterator

import net.pearx.okservable.collection.ObservableListHandler
import net.pearx.okservable.collection.ObservableListEvent
import net.pearx.okservable.collection.ObservableListScope
import net.pearx.okservable.collection.send
import kotlin.math.max

class ObservableMutableListIterator<T>(private val base: MutableListIterator<T>, private val onUpdate: ObservableListHandler<T>) : MutableListIterator<T> by base {
    private var lastElement: T? = null
    private var lastElementIndex: Int = -1

    override fun next(): T {
        if(hasNext())
            lastElementIndex = nextIndex()
        return base.next().also {
            lastElement = it
        }
    }

    override fun previous(): T {
        if(hasPrevious())
            lastElementIndex = previousIndex()
        return base.previous().also {
            lastElement = it
        }
    }

    override fun add(element: T) {
        base.add(element)
        onUpdate.send(ObservableListEvent.ElementAdded(max(0, lastElementIndex), element))
    }

    override fun remove() {
        base.remove()
        onUpdate.send(ObservableListEvent.ElementRemoved(lastElementIndex, lastElement as T))
    }

    override fun set(element: T) {
        base.set(element)
        if(lastElement != element)
            onUpdate.send(ObservableListEvent.ElementSet(lastElementIndex, lastElement as T, element))
    }
}