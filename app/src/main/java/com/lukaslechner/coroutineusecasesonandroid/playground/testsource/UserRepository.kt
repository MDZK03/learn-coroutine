package com.lukaslechner.coroutineusecasesonandroid.playground.testsource

/*
 * Copyright 2022 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import kotlinx.coroutines.delay

/**
 * Simple example class to be tested
 */
open class UserRepository {
    private var users = mutableListOf<String>()

    open suspend fun register(name: String) {
        delay(100L)
        users += name
        println("Registered $name")
    }

    open suspend fun getAllUsers(): List<String> {
        delay(100L)
        return users
    }
}