package org.wolkern.cgg

import kotlinx.browser.localStorage

 class KeyValueStorage(directoryPath: String) {
     fun saveString(key: String, value: String) {
        localStorage.setItem(key, value)
    }

     fun getString(key: String): String? {
        return localStorage.getItem(key)
    }
}