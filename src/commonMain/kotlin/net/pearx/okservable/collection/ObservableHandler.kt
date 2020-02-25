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

interface ObservableMapHandler<K, V> {
    fun onClear(elements: Map<K, V>)
    fun onPut(key: K, prevValue: V?, value: V)
    fun onRemove(key: K, value: V)
}

private typealias ClearBlock<T> = (elements: Collection<T>) -> Unit

abstract class AbstractObservableCollectionHandlerScope<T> {
    private var clearBlock: ClearBlock<T>? = null

    fun clear(block: ClearBlock<T>) {
        clearBlock = block
    }

    protected abstract inner class Handler : AbstractObservableCollectionHandler<T> {
        override fun onClear(elements: Collection<T>) {
            clearBlock?.invoke(elements)
        }
    }
}

private typealias CollectionElementBlock<T> = (element: T) -> Unit

class ObservableCollectionHandlerScope<T> : AbstractObservableCollectionHandlerScope<T>() {
    private var addBlock: CollectionElementBlock<T>? = null
    private var removeBlock: CollectionElementBlock<T>? = null

    fun add(block: CollectionElementBlock<T>) {
        addBlock = block
    }

    fun remove(block: CollectionElementBlock<T>) {
        removeBlock = block
    }

    private inner class Handler : AbstractObservableCollectionHandlerScope<T>.Handler(), ObservableCollectionHandler<T> {
        override fun onAdd(element: T) {
            addBlock?.invoke(element)
        }

        override fun onRemove(element: T) {
            removeBlock?.invoke(element)
        }
    }

    @PublishedApi
    internal fun createHandler(): ObservableCollectionHandler<T> = Handler()
}


private typealias ListElementBlock<T> = (index: Int, element: T) -> Unit
private typealias ListSetBlock<T> = (index: Int, prevElement: T, newElement: T) -> Unit


class ObservableListHandlerScope<T> : AbstractObservableCollectionHandlerScope<T>() {
    private var addBlock: ListElementBlock<T>? = null
    private var removeBlock: ListElementBlock<T>? = null
    private var setBlock: ListSetBlock<T>? = null

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
            addBlock?.invoke(index, element)
        }

        override fun onRemove(index: Int, element: T) {
            removeBlock?.invoke(index, element)
        }

        override fun onSet(index: Int, prevValue: T, newValue: T) {
            setBlock?.invoke(index, prevValue, newValue)
        }
    }

    @PublishedApi
    internal fun createHandler(): ObservableListHandler<T> = Handler()
}

private typealias MapClearBlock<K, V> = (elements: Map<K, V>) -> Unit
private typealias MapPutBlock<K, V> = (key: K, prevValue: V?, value: V) -> Unit
private typealias MapRemoveBlock<K, V> = (key: K, value: V) -> Unit

class ObservableMapHandlerScope<K, V> {
    private var clearBlock: MapClearBlock<K, V>? = null
    private var putBlock: MapPutBlock<K, V>? = null
    private var removeBlock: MapRemoveBlock<K, V>? = null

    fun clear(block: MapClearBlock<K, V>) {
        clearBlock = block
    }

    fun put(block: MapPutBlock<K, V>) {
        putBlock = block
    }

    fun remove(block: MapRemoveBlock<K, V>) {
        removeBlock = block
    }

    private inner class Handler : ObservableMapHandler<K, V> {
        override fun onClear(elements: Map<K, V>) {
            clearBlock?.invoke(elements)
        }

        override fun onPut(key: K, prevValue: V?, value: V) {
            putBlock?.invoke(key, prevValue, value)
        }

        override fun onRemove(key: K, value: V) {
            removeBlock?.invoke(key, value)
        }
    }

    @PublishedApi
    internal fun createHandler(): ObservableMapHandler<K, V> = Handler()
}