package com.example.curity.config;

import se.curity.identityserver.sdk.config.Configuration
import se.curity.identityserver.sdk.config.annotation.DefaultInteger
import se.curity.identityserver.sdk.config.annotation.DefaultString
import se.curity.identityserver.sdk.config.annotation.Description
import se.curity.identityserver.sdk.service.ExceptionFactory
import se.curity.identityserver.sdk.service.HttpClient
import se.curity.identityserver.sdk.service.Json
import se.curity.identityserver.sdk.service.WebServiceClientFactory

interface OpaAuthorizationManagerPluginConfig : Configuration
{
    @Description("An Http Client to use for the connection with OPA")
    fun getHttpClient(): HttpClient

    @Description("The OPA hostname")
    fun getOpaHost(): String

    @Description("The port where OPA is exposed")
    @DefaultInteger(8181)
    fun getOpaPort(): Int

    @Description("The OPA path")
    @DefaultString("/v1/data/curity/")
    fun getOpaPath(): String

    fun getExceptionFactory(): ExceptionFactory
    fun getJson(): Json
    fun getWebServiceClientFactory(): WebServiceClientFactory
}
