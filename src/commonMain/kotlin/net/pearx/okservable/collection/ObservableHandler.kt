/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.okservable.collection

typealias ObservableHandlerSimple = () -> Unit


interface AbstractObservableCollectionHandler<T> {
    fun onClear(elements: Collection<T>)
}

interface ObservableCollectionHandler<T> : AbstractObservableCollectionHandler<T> {
    fun onAdd(element: T)
    fun onRemove(element: T)
}

interface ObservableListHandler<T> : AbstractObservableCollectionHandler<T> {
    fun onAdd(index: Int, element: T)
    fun onRemove(index: Int, element: T)
    fun onSet(index: Int, prevValue: T, newValue: T)
}


private typealias ClearBlock<T> = (elements: Collection<T>) -> Unit

abstract class AbstractObservableCollectionHandlerScope<T> {
    private lateinit var clearBlock: ClearBlock<T>

    fun clear(block: ClearBlock<T>) {
        clearBlock = block
    }

    protected abstract inner class Handler : AbstractObservableCollectionHandler<T> {
        override fun onClear(elements: Collection<T>) {
            clearBlock(elements)
        }
    }
}

private typealias CollectionElementBlock<T> = (element: T) -> Unit

class ObservableCollectionHandlerScope<T> : AbstractObservableCollectionHandlerScope<T>() {
    private lateinit var addBlock: CollectionElementBlock<T>
    private lateinit var removeBlock: CollectionElementBlock<T>

    fun add(block: CollectionElementBlock<T>) {
        addBlock = block
    }

    fun remove(block: CollectionElementBlock<T>) {
        removeBlock = block
    }

    private inner class Handler : AbstractObservableCollectionHandlerScope<T>.Handler(), ObservableCollectionHandler<T> {
        override fun onAdd(element: T) {
            addBlock(element)
        }

        override fun onRemove(element: T) {
            removeBlock(element)
        }
    }

    @PublishedApi
    internal fun createHandler(): ObservableCollectionHandler<T> = Handler()
}


private typealias ListElementBlock<T> = (index: Int, element: T) -> Unit
private typealias ListSetBlock<T> = (index: Int, prevElement: T, newElement: T) -> Unit


class ObservableListHandlerScope<T> : AbstractObservableCollectionHandlerScope<T>() {
    private lateinit var addBlock: ListElementBlock<T>
    private lateinit var removeBlock: ListElementBlock<T>
    private lateinit var setBlock: ListSetBlock<T>

    fun add(block: ListElementBlock<T>) {
        addBlock = block
    }

    fun remove(block: ListElementBlock<T>) {
        removeBlock = block
    }

    fun set(block: ListSetBlock<T>) {
        setBlock = block
    }

    private inner class Handler : AbstractObservableCollectionHandlerScope<T>.Handler(), ObservableListHandler<T> {
        override fun onAdd(index: Int, element: T) {
            addBlock(index, element)
        }

        override fun onRemove(index: Int, element: T) {
            removeBlock(index, element)
        }

        override fun onSet(index: Int, prevValue: T, newValue: T) {
            setBlock(index, prevValue, newValue)
        }
    }

    @PublishedApi
    internal fun createHandler(): ObservableListHandler<T> = Handler()
}