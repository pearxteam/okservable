/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.okservable.test

import net.pearx.okservable.collection.observableSet
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ObservableSetTest {
    enum class Action {
        ADD,
        REMOVE,
        CLEAR
    }

    data class Modification(val action: Action, val elements: Collection<String?>) {
        constructor(action: Action, element: String?) : this(action, listOf(element))
    }

    inner class TestingContext(empty: Boolean = false) {
        private val _modifications = mutableListOf<Modification>()
        val modifications: List<Modification>
            get() = _modifications
        
        val base = if (empty) mutableSetOf() else mutableSetOf("theevilroot", "root", null, "")

        val collection = base.observableSet {
            add { element ->
                _modifications += Modification(Action.ADD, element)
            }
            remove { element ->
                _modifications += Modification(Action.REMOVE, element)
            }
            clear { elements ->
                _modifications += Modification(Action.CLEAR, elements)
            }
        }
    }

    @Test
    fun testRandomAccess() {
        with(TestingContext()) {
            assertEquals(false, collection is RandomAccess)
        }
    }

    @Test
    fun testNotChangedMethodsNonEmpty() {
        with(TestingContext()) {
            with(collection) {
                assertEquals(4, size)

                assertEquals(true, contains(null))
                assertEquals(true, contains("root"))
                assertEquals(false, contains("doesnt-exist"))

                assertEquals(false, containsAll(listOf(null, "STRING")))
                assertEquals(false, containsAll(listOf("doesnt-exist-too", "doesnt-exist")))
                assertEquals(true, containsAll(listOf("root", "theevilroot")))

                assertEquals(false, isEmpty())

                assertEquals("[theevilroot, root, null, ]", toString())
                assertEquals(setOf("theevilroot", "root", null, "").hashCode(), hashCode())
                assertTrue(this == setOf("theevilroot", "root", null, ""))
            }
            assertEquals(listOf(), modifications)
        }

    }

    @Test
    fun testNotChangedMethodsEmpty() {
        with(TestingContext(true)) {
            with(collection) {
                assertEquals(0, size)

                assertEquals(true, isEmpty())

                assertEquals("[]", toString())
                assertEquals(setOf<String?>().hashCode(), hashCode())
                assertEquals<Collection<String?>>(this, setOf())
            }
            assertEquals(listOf(), modifications)
        }
    }

    @Test
    fun testAdd() {
        with(TestingContext()) {
            collection.add("theevilroot")
            assertEquals(listOf(), modifications)
            assertEquals(setOf<String?>("theevilroot", "root", null, ""), base)
        }

        with(TestingContext()) {
            collection.add("theevilroot1")
            assertEquals(listOf(Modification(Action.ADD, "theevilroot1")), modifications)
            assertEquals(setOf<String?>("theevilroot", "root", null, "", "theevilroot1"), base)
        }
    }

    @Test
    fun testAddAll() {
        with(TestingContext()) {
            collection.addAll(listOf(null, "root"))
            assertEquals(listOf(), modifications)
            assertEquals(setOf<String?>("theevilroot", "root", null, ""), base)
        }

        with(TestingContext()) {
            collection.addAll(listOf("theevilroot", "theevilroot1"))
            assertEquals(listOf(Modification(Action.ADD, "theevilroot1")), modifications)
            assertEquals(setOf<String?>("theevilroot", "root", null, "", "theevilroot1"), base)
        }

        with(TestingContext()) {
            collection.addAll(listOf("openwrt", "ddwrt"))
            assertEquals(listOf(Modification(Action.ADD, "openwrt"), Modification(Action.ADD, "ddwrt")), modifications)
            assertEquals(setOf<String?>("theevilroot", "root", null, "", "openwrt", "ddwrt"), base)
        }
    }

    @Test
    fun testClear() {
        with(TestingContext(true)) {
            collection.clear()
            assertEquals(listOf(), modifications)
            assertEquals(setOf<String?>(), base)
        }

        with(TestingContext()) {
            collection.clear()
            assertEquals(listOf(Modification(Action.CLEAR, listOf("theevilroot", "root", null, ""))), modifications)
            assertEquals(setOf<String?>(), base)
        }
    }

    @Test
    fun testIterator() {
        with(TestingContext()) {
            val iter = collection.iterator()
            var num = 0
            for (element in iter) {
                num++
            }
            assertEquals(4, num)
            assertEquals(listOf(), modifications)
            assertEquals(setOf<String?>("theevilroot", "root", null, ""), base)
        }

        with(TestingContext()) {
            val iter = collection.iterator()
            var num = 0
            for (element in iter) {
                if (element == null)
                    iter.remove()
                num++
            }
            assertEquals(4, num)
            assertEquals(listOf(Modification(Action.REMOVE, null)), modifications)
            assertEquals(setOf<String?>("theevilroot", "root", ""), base)
        }
    }

    @Test
    fun testRemove() {
        with(TestingContext()) {
            collection.remove("theevilroot1")
            assertEquals(listOf(), modifications)
            assertEquals(setOf<String?>("theevilroot", "root", null, ""), base)
        }

        with(TestingContext()) {
            collection.remove("theevilroot")
            assertEquals(listOf(Modification(Action.REMOVE, "theevilroot")), modifications)
            assertEquals(setOf<String?>("root", null, ""), base)
        }
    }

    @Test
    fun testRemoveAll() {
        with(TestingContext()) {
            collection.removeAll(listOf("openwrt", "ddwrt"))
            assertEquals(listOf(), modifications)
            assertEquals(setOf<String?>("theevilroot", "root", null, ""), base)
        }

        with(TestingContext()) {
            collection.removeAll(listOf(null, "root"))
            assertEquals(listOf(Modification(Action.REMOVE, "root"), Modification(Action.REMOVE, null)), modifications)
            assertEquals(setOf<String?>("theevilroot", ""), base)
        }

        with(TestingContext()) {
            collection.removeAll(listOf("theevilroot", "theevilroot1"))
            assertEquals(listOf(Modification(Action.REMOVE, "theevilroot")), modifications)
            assertEquals(setOf<String?>("root", null, ""), base)
        }
    }

    @Test
    fun testRetainAll() {
        with(TestingContext()) {
            collection.retainAll(listOf("theevilroot", "root", null, ""))
            assertEquals(listOf(), modifications)
            assertEquals(setOf<String?>("theevilroot", "root", null, ""), base)
        }

        with(TestingContext()) {
            collection.retainAll(listOf(null, "", "theevilroot1"))
            assertEquals(listOf(Modification(Action.REMOVE, "theevilroot"), Modification(Action.REMOVE, "root")), modifications)
            assertEquals(setOf<String?>(null, ""), base)
        }

        with(TestingContext()) {
            collection.retainAll(listOf("theevilroot1"))
            assertEquals(listOf(Modification(Action.REMOVE, "theevilroot"), Modification(Action.REMOVE, "root"), Modification(Action.REMOVE, null), Modification(Action.REMOVE, "")), modifications)
            assertEquals(setOf<String?>(), base)
        }
    }
}