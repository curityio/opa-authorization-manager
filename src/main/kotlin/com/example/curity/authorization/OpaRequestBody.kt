package com.example.curity.authorization

class OpaRequestBody(
    group: String?,
    subject: String?,
    resourceType: String?
)
{
    val input = mapOf(
        Pair("groups", group),
        Pair("subject", subject),
        Pair("resourceType", resourceType)
    )
}