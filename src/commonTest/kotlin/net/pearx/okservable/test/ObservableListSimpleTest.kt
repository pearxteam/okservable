/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.okservable.test

import net.pearx.okservable.collection.observableListSimple
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ObservableListSimpleTest {
    inner class TestingContext(empty: Boolean = false) {
        var modified = false
        val base = if(empty) mutableListOf() else mutableListOf("theevilroot", "root", null, "", "root")
        val collection = base.observableListSimple {
            modified = true
        }
    }

    @Test
    fun testRandomAccess() {
        with(TestingContext()) {
            assertEquals(true, collection is RandomAccess)
        }
    }

    @Test
    fun testNotChangedMethodsNonEmpty() {
        with(TestingContext()) {
            with(collection) {
                assertEquals(5, size)

                assertEquals(true, contains(null))
                assertEquals(true, contains("root"))
                assertEquals(false, contains("doesnt-exist"))

                assertEquals(false, containsAll(listOf(null, "STRING")))
                assertEquals(false, containsAll(listOf("doesnt-exist-too", "doesnt-exist")))
                assertEquals(true, containsAll(listOf("root", "theevilroot")))

                assertEquals(false, isEmpty())

                assertEquals("[theevilroot, root, null, , root]", toString())
                assertEquals(listOf("theevilroot", "root", null, "", "root").hashCode(), hashCode())
                assertEquals(this, listOf("theevilroot", "root", null, "", "root"))

                assertEquals("root", get(1))
                assertEquals(1, indexOf("root"))
                assertEquals(4, lastIndexOf("root"))
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
                assertEquals(listOf<String?>().hashCode(), hashCode())
                assertEquals(this, listOf<String?>())
            }
            assertEquals(false, modified)
        }
    }

    @Test
    fun testAdd() {
        with(TestingContext()) {
            collection.add("theevilroot")
            assertEquals(true, modified)
            assertEquals(listOf<String?>("theevilroot", "root", null, "", "root", "theevilroot"), base)
        }

        with(TestingContext()) {
            collection.add("theevilroot1")
            assertEquals(true, modified)
            assertEquals(listOf<String?>("theevilroot", "root", null, "", "root", "theevilroot1"), base)
        }
    }

    @Test
    fun testAddWithIndex() {
        with(TestingContext()) {
            collection.add(2, "theevilroot1")
            assertEquals(true, modified)
            assertEquals(listOf<String?>("theevilroot", "root", "theevilroot1", null, "", "root"), base)
        }

        with(TestingContext()) {
            collection.add(0, "theevilroot")
            assertEquals(true, modified)
            assertEquals(listOf<String?>("theevilroot", "theevilroot", "root", null, "", "root"), base)
        }

        with(TestingContext()) {
            assertFailsWith<IndexOutOfBoundsException> { collection.add(6, "theevilroot") }
            assertEquals(false, modified)
            assertEquals(listOf<String?>("theevilroot", "root", null, "", "root"), base)
        }
    }

    @Test
    fun testAddAll() {
        with(TestingContext()) {
            collection.addAll(listOf(null, "root"))
            assertEquals(true, modified)
            assertEquals(listOf<String?>("theevilroot", "root", null, "", "root", null, "root"), base)
        }

        with(TestingContext()) {
            collection.addAll(listOf("theevilroot", "theevilroot1"))
            assertEquals(true, modified)
            assertEquals(listOf<String?>("theevilroot", "root", null, "", "root", "theevilroot", "theevilroot1"), base)
        }

        with(TestingContext()) {
            collection.addAll(listOf("openwrt", "ddwrt"))
            assertEquals(true, modified)
            assertEquals(listOf<String?>("theevilroot", "root", null, "", "root", "openwrt", "ddwrt"), base)
        }

        with(TestingContext()) {
            collection.addAll(listOf())
            assertEquals(false, modified)
            assertEquals(listOf<String?>("theevilroot", "root", null, "", "root"), base)
        }
    }

    @Test
    fun testAddAllWithIndex() {
        with(TestingContext()) {
            collection.addAll(0, listOf(null, "root"))
            assertEquals(true, modified)
            assertEquals(listOf<String?>(null, "root", "theevilroot", "root", null, "", "root"), base)
        }

        with(TestingContext()) {
            collection.addAll(5, listOf("openwrt", "ddwrt"))
            assertEquals(true, modified)
            assertEquals(listOf<String?>("theevilroot", "root", null, "", "root", "openwrt", "ddwrt"), base)
        }

        with(TestingContext()) {
            collection.addAll(2, listOf())
            assertEquals(false, modified)
            assertEquals(listOf<String?>("theevilroot", "root", null, "", "root"), base)
        }

        with(TestingContext()) {
            assertFailsWith<IndexOutOfBoundsException> { collection.addAll(6, listOf("theevilroot")) }
            assertEquals(false, modified)
            assertEquals(listOf<String?>("theevilroot", "root", null, "", "root"), base)
        }
    }

    @Test
    fun testClear() {
        with(TestingContext(true)) {
            collection.clear()
            assertEquals(false, modified)
            assertEquals(listOf<String?>(), base)
        }

        with(TestingContext()) {
            collection.clear()
            assertEquals(true, modified)
            assertEquals(listOf<String?>(), base)
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
            assertEquals(5, num)
            assertEquals(false, modified)
            assertEquals(listOf<String?>("theevilroot", "root", null, "", "root"), base)
        }

        with(TestingContext()) {
            val iter = collection.iterator()
            var num = 0
            for(element in iter) {
                if(element == null)
                    iter.remove()
                num++
            }
            assertEquals(5, num)
            assertEquals(true, modified)
            assertEquals(listOf<String?>("theevilroot", "root", "", "root"), base)
        }
    }

    @Test
    fun testListIterator() {
        with(TestingContext()) {
            val iter = collection.listIterator()
            val indices = mutableListOf<Int>()
            val elements = mutableListOf<String?>()
            var num = 0
            while(iter.hasNext()) {
                indices += iter.nextIndex()
                elements += iter.next()
                num++
            }
            assertEquals(5, num)
            assertEquals(false, modified)
            assertEquals(listOf<String?>("theevilroot", "root", null, "", "root"), elements)
            assertEquals((0..4).toList(), indices)
            assertEquals(listOf<String?>("theevilroot", "root", null, "", "root"), base)
        }

        with(TestingContext()) {
            val iter = collection.listIterator()
            val indices = mutableListOf<Int>()
            val elements = mutableListOf<String?>()
            var num = 0
            while(iter.hasNext()) {
                indices += iter.nextIndex()
                val el = iter.next()
                if(el == "theevilroot")
                    iter.add("five")
                if(el == "")
                    iter.set("cake")
                if(el == null)
                    iter.remove()
                elements += el
                num++
            }
            assertEquals(5, num)
            assertEquals(true, modified)
            assertEquals(listOf<String?>("theevilroot", "root", null, "", "root"), elements)
            assertEquals(listOf(0, 2, 3, 3, 4), indices)
            assertEquals(listOf<String?>("theevilroot", "five", "root", "cake", "root"), base)
        }
    }

    @Test
    fun testListIteratorFromIndex() {
        with(TestingContext()) {
            val iter = collection.listIterator(5)
            val indices = mutableListOf<Int>()
            val elements = mutableListOf<String?>()
            var num = 0
            while(iter.hasPrevious()) {
                indices += iter.previousIndex()
                elements += iter.previous()
                num++
            }
            assertEquals(5, num)
            assertEquals(false, modified)
            assertEquals(listOf<String?>("root", "", null, "root", "theevilroot"), elements)
            assertEquals((4 downTo 0).toList(), indices)
            assertEquals(listOf<String?>("theevilroot", "root", null, "", "root"), base)
        }

        with(TestingContext()) {
            val iter = collection.listIterator(5)
            val indices = mutableListOf<Int>()
            val elements = mutableListOf<String?>()
            var num = 0
            while(iter.hasPrevious()) {
                indices += iter.previousIndex()
                val el = iter.previous()
                if(el == "theevilroot")
                    iter.add("five")
                if(el == "")
                    iter.set("cake")
                if(el == null)
                    iter.remove()
                elements += el
                num++
            }
            assertEquals(6, num)
            assertEquals(true, modified)
            assertEquals(listOf<String?>("root", "", null, "root", "theevilroot", "five"), elements)
            assertEquals(listOf(4, 3, 2, 1, 0, 0), indices)
            assertEquals(listOf<String?>("five", "theevilroot", "root", "cake", "root"), base)
        }
    }

    @Test
    fun testRemove() {
        with(TestingContext()) {
            collection.remove("theevilroot1")
            assertEquals(false, modified)
            assertEquals(listOf<String?>("theevilroot", "root", null, "", "root"), base)
        }

        with(TestingContext()) {
            collection.remove("theevilroot")
            assertEquals(true, modified)
            assertEquals(listOf<String?>("root", null, "", "root"), base)
        }
    }

    @Test
    fun testRemoveAt() {
        with(TestingContext()) {
            collection.removeAt(0)
            assertEquals(true, modified)
            assertEquals(listOf<String?>("root", null, "", "root"), base)
        }

        with(TestingContext()) {
            assertFailsWith<IndexOutOfBoundsException> { collection.removeAt(5) }
            assertEquals(false, modified)
            assertEquals(listOf<String?>("theevilroot", "root", null, "", "root"), base)
        }
    }

    @Test
    fun testSet() {
        with(TestingContext()) {
            collection[0] = "tea"
            assertEquals(true, modified)
            assertEquals(listOf<String?>("tea", "root", null, "", "root"), base)
        }

        with(TestingContext()) {
            assertFailsWith<IndexOutOfBoundsException> { collection[5] = "povar" }
            assertEquals(false, modified)
            assertEquals(listOf<String?>("theevilroot", "root", null, "", "root"), base)
        }
    }

    @Test
    fun testRemoveAll() {
        with(TestingContext()) {
            collection.removeAll(listOf("openwrt", "ddwrt"))
            assertEquals(false, modified)
            assertEquals(listOf<String?>("theevilroot", "root", null, "", "root"), base)
        }

        with(TestingContext()) {
            collection.removeAll(listOf(null, "root"))
            assertEquals(true, modified)
            assertEquals(listOf<String?>("theevilroot", ""), base)
        }

        with(TestingContext()) {
            collection.removeAll(listOf("theevilroot", "theevilroot1"))
            assertEquals(true, modified)
            assertEquals(listOf<String?>("root", null, "", "root"), base)
        }
    }

    @Test
    fun testRetainAll() {
        with(TestingContext()) {
            collection.retainAll(listOf("theevilroot", "root", null, "", "root"))
            assertEquals(false, modified)
            assertEquals(listOf<String?>("theevilroot", "root", null, "", "root"), base)
        }

        with(TestingContext()) {
            collection.retainAll(listOf(null, "", "theevilroot1"))
            assertEquals(true, modified)
            assertEquals(listOf<String?>(null, ""), base)
        }

        with(TestingContext()) {
            collection.retainAll(listOf("theevilroot1"))
            assertEquals(true, modified)
            assertEquals(listOf<String?>(), base)
        }
    }
}