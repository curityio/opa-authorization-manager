/*
 *  Copyright 2023 Curity AB
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.example.curity.authorization

import java.util.ArrayList

class OpaResponse(
    val allow: Boolean,
    val unauthorizedFields: ArrayList<String>?
)

fun Map<String, Any>.toOpaResponse(): OpaResponse
{
    with(this["result"] as Map<*, *>)
    {
        return@toOpaResponse OpaResponse(
            this["allow"] as Boolean,
            this["unauthorized_fields"] as ArrayList<String>?
        )
    }
}