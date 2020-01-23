/*
 * Copyright © 2020, PearX Team
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.pearx.okservable.internal

internal inline fun Boolean.ifTrue(block: () -> Unit): Boolean = also { if (it) block() }

internal fun <C : MutableCollection<E>, E> C.removeBulk(elements: Collection<E>, remove: Boolean): Boolean {
        val it = iterator()
        var modified = false
        for (el in it)
            if (el in elements == remove) {
                it.remove()
                modified = true
            }
        return modified
}