package com.example.curity.config;

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
