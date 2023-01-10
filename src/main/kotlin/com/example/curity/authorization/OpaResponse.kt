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