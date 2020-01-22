/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.okservable.test

import net.pearx.okservable.collection.observableSetSimpleBy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ObservableSetSimpleTest {
    inner class TestingContext(empty: Boolean = false) {
        var modified = false
        var collection = observableSetSimpleBy(if(empty) mutableSetOf() else mutableSetOf("theevilroot", "root", null, "")) { modified = true }
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
            assertEquals(false, modified)
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
            assertEquals(false, modified)
        }
    }

    @Test
    fun testAdd() {
        with(TestingContext()) {
            collection.add("theevilroot")
            assertEquals(false, modified)
            assertEquals(setOf<String?>("theevilroot", "root", null, ""), collection.base)
        }

        with(TestingContext()) {
            collection.add("theevilroot1")
            assertEquals(true, modified)
            assertEquals(setOf<String?>("theevilroot", "root", null, "", "theevilroot1"), collection.base)
        }
    }

    @Test
    fun testAddAll() {
        with(TestingContext()) {
            collection.addAll(listOf(null, "root"))
            assertEquals(false, modified)
            assertEquals(setOf<String?>("theevilroot", "root", null, ""), collection.base)
        }

        with(TestingContext()) {
            collection.addAll(listOf("theevilroot", "theevilroot1"))
            assertEquals(true, modified)
            assertEquals(setOf<String?>("theevilroot", "root", null, "", "theevilroot1"), collection.base)
        }

        with(TestingContext()) {
            collection.addAll(listOf("openwrt", "ddwrt"))
            assertEquals(true, modified)
            assertEquals(setOf<String?>("theevilroot", "root", null, "", "openwrt", "ddwrt"), collection.base)
        }
    }

    @Test
    fun testClear() {
        with(TestingContext(true)) {
            collection.clear()
            assertEquals(false, modified)
            assertEquals(setOf<String?>(), collection.base)
        }

        with(TestingContext()) {
            collection.clear()
            assertEquals(true, modified)
            assertEquals(setOf<String?>(), collection.base)
        }
    }

    @Test
    fun testIterator() {
        with(TestingContext()) {
            val iter = collection.iterator()
            var num = 0
            for(element in iter) {
                num++
            }
            assertEquals(4, num)
            assertEquals(false, modified)
            assertEquals(setOf<String?>("theevilroot", "root", null, ""), collection.base)
        }

        with(TestingContext()) {
            val iter = collection.iterator()
            var num = 0
            for(element in iter) {
                if(element == null)
                    iter.remove()
                num++
            }
            assertEquals(4, num)
            assertEquals(true, modified)
            assertEquals(setOf<String?>("theevilroot", "root", ""), collection.base)
        }
    }

    @Test
    fun testRemove() {
        with(TestingContext()) {
            collection.remove("theevilroot1")
            assertEquals(false, modified)
            assertEquals(setOf<String?>("theevilroot", "root", null, ""), collection.base)
        }

        with(TestingContext()) {
            collection.remove("theevilroot")
            assertEquals(true, modified)
            assertEquals(setOf<String?>("root", null, ""), collection.base)
        }
    }

    @Test
    fun testRemoveAll() {
        with(TestingContext()) {
            collection.removeAll(listOf("openwrt", "ddwrt"))
            assertEquals(false, modified)
            assertEquals(setOf<String?>("theevilroot", "root", null, ""), collection.base)
        }

        with(TestingContext()) {
            collection.removeAll(listOf(null, "root"))
            assertEquals(true, modified)
            assertEquals(setOf<String?>("theevilroot", ""), collection.base)
        }

        with(TestingContext()) {
            collection.removeAll(listOf("theevilroot", "theevilroot1"))
            assertEquals(true, modified)
            assertEquals(setOf<String?>("root", null, ""), collection.base)
        }
    }

    @Test
    fun testRetainAll() {
        with(TestingContext()) {
            collection.retainAll(listOf("theevilroot", "root", null, ""))
            assertEquals(false, modified)
            assertEquals(setOf<String?>("theevilroot", "root", null, ""), collection.base)
        }

        with(TestingContext()) {
            collection.retainAll(listOf(null, "", "theevilroot1"))
            assertEquals(true, modified)
            assertEquals(setOf<String?>(null, ""), collection.base)
        }

        with(TestingContext()) {
            collection.retainAll(listOf("theevilroot1"))
            assertEquals(true, modified)
            assertEquals(setOf<String?>(), collection.base)
        }
    }
}