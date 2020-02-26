/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.okservable.test

import net.pearx.okservable.collection.observableMapSimple
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ObservableMapSimpleTest {
    inner class TestingContext(empty: Boolean = false) {
        var modified = false
        val base = if(empty) mutableMapOf() else mutableMapOf("theevilroot" to "root", null to "", "" to null, "superuser" to "root")
        var map = base.observableMapSimple { modified = true }
    }

    @Test
    fun testNotChangedMethodsNonEmpty() {
        with(TestingContext()) {
            with(map) {
                assertEquals(4, size)

                assertEquals(true, containsKey(null))
                assertEquals(true, containsKey("theevilroot"))
                assertEquals(true, containsKey(""))
                assertEquals(false, containsKey("doesnt-exist"))

                assertEquals(true, containsValue(null))
                assertEquals(true, containsValue(""))
                assertEquals(true, containsValue("root"))
                assertEquals(false, containsValue("theevilroot"))
                assertEquals(false, containsValue("something"))

                assertEquals(false, isEmpty())

                assertEquals("root", get("theevilroot"))
                assertEquals(null, get(""))
                assertEquals(null, get("non-existing"))

                assertEquals("root", getOrDefault("theevilroot", "default"))
                assertEquals(null, getOrDefault("", "default"))
                assertEquals("default", getOrDefault("non-existing", "default"))

                assertEquals("{theevilroot=root, null=, =null, superuser=root}", toString())
                assertEquals(mutableMapOf("theevilroot" to "root", null to "", "" to null, "superuser" to "root").hashCode(), hashCode())
                assertTrue(this == mutableMapOf("theevilroot" to "root", null to "", "" to null, "superuser" to "root"))
            }
            assertEquals(false, modified)
        }

    }

    @Test
    fun testNotChangedMethodsEmpty() {
        with(TestingContext(true)) {
            with(map) {
                assertEquals(0, size)

                assertEquals(true, isEmpty())

                assertEquals("{}", toString())
                assertEquals(mutableMapOf<String?, String?>().hashCode(), hashCode())
                assertEquals<MutableMap<String?, String?>>(this, mutableMapOf())
            }
            assertEquals(false, modified)
        }
    }

    @Test
    fun testPut() {
        with(TestingContext()) {
            map["theevilroot"] = "root"
            assertEquals(false, modified)
            assertEquals(mapOf("theevilroot" to "root", null to "", "" to null, "superuser" to "root"), base)
        }

        with(TestingContext()) {
            map["theevilroot"] = "root1"
            assertEquals(true, modified)
            assertEquals(mapOf("theevilroot" to "root1", null to "", "" to null, "superuser" to "root"), base)
        }

        with(TestingContext()) {
            map["theevilroot1"] = "11"
            assertEquals(true, modified)
            assertEquals(mapOf("theevilroot" to "root", null to "", "" to null, "superuser" to "root", "theevilroot1" to "11"), base)
        }
    }

    @Test
    fun testPutAll() {
        with(TestingContext()) {
            map.putAll(mapOf("theevilroot" to "root", "" to null))
            assertEquals(false, modified)
            assertEquals(mapOf("theevilroot" to "root", null to "", "" to null, "superuser" to "root"), base)
        }

        with(TestingContext()) {
            map.putAll(mapOf("theevilroot" to "root1", "" to null))
            assertEquals(true, modified)
            assertEquals(mapOf("theevilroot" to "root1", null to "", "" to null, "superuser" to "root"), base)
        }

        with(TestingContext()) {
            map.putAll(mapOf("theevilroot" to "root1", "cake" to "bake"))
            assertEquals(true, modified)
            assertEquals(mapOf("theevilroot" to "root1", null to "", "" to null, "superuser" to "root", "cake" to "bake"), base)
        }
    }

    @Test
    fun testRemove() {
        with(TestingContext()) {
            map.remove("non-existing")
            assertEquals(false, modified)
            assertEquals(mapOf("theevilroot" to "root", null to "", "" to null, "superuser" to "root"), base)
        }

        with(TestingContext()) {
            map.remove("theevilroot")
            assertEquals(true, modified)
            assertEquals(mapOf(null to "", "" to null, "superuser" to "root"), base)
        }
    }

    @Test
    fun testRemovWithValue() {
        with(TestingContext()) {
            map.remove("non-existing", "abc")
            assertEquals(false, modified)
            assertEquals(mapOf("theevilroot" to "root", null to "", "" to null, "superuser" to "root"), base)
        }

        with(TestingContext()) {
            map.remove("theevilroot", "root1")
            assertEquals(false, modified)
            assertEquals(mapOf("theevilroot" to "root", null to "", "" to null, "superuser" to "root"), base)
        }

        with(TestingContext()) {
            map.remove("theevilroot", "root")
            assertEquals(true, modified)
            assertEquals(mapOf(null to "", "" to null, "superuser" to "root"), base)
        }
    }

    @Test
    fun testClear() {
        with(TestingContext(true)) {
            map.clear()
            assertEquals(false, modified)
            assertEquals(mapOf<String?, String?>(), base)
        }

        with(TestingContext()) {
            map.clear()
            assertEquals(true, modified)
            assertEquals(mapOf<String?, String?>(), base)
        }
    }
}