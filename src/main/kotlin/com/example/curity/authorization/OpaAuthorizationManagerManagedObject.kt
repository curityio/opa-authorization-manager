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

import com.example.curity.config.OpaAuthorizationManagerPluginConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import se.curity.identityserver.sdk.attribute.ContextAttributes
import se.curity.identityserver.sdk.attribute.SubjectAttributes
import se.curity.identityserver.sdk.authorization.AuthorizationResult
import se.curity.identityserver.sdk.authorization.GraphQLObligation
import se.curity.identityserver.sdk.authorization.graphql.GraphQLAuthorizationActionAttributes
import se.curity.identityserver.sdk.authorization.graphql.GraphQLAuthorizationResourceAttributes
import se.curity.identityserver.sdk.errors.ErrorCode
import se.curity.identityserver.sdk.http.HttpRequest
import se.curity.identityserver.sdk.http.HttpResponse
import se.curity.identityserver.sdk.plugin.ManagedObject
import se.curity.identityserver.sdk.service.ExceptionFactory
import se.curity.identityserver.sdk.service.HttpClient
import se.curity.identityserver.sdk.service.Json
import se.curity.identityserver.sdk.service.WebServiceClient
import java.io.IOException
import java.time.Duration

class OpaAuthorizationManagerManagedObject(configuration: OpaAuthorizationManagerPluginConfig) :
    ManagedObject<OpaAuthorizationManagerPluginConfig?>(configuration)
{
    private val json: Json = configuration.getJson()
    private val exceptionFactory: ExceptionFactory = configuration.getExceptionFactory()
    private val logger: Logger = LoggerFactory.getLogger(OpaAuthorizationManagerManagedObject::class.java)
    private val host: String = configuration.getOpaHost()
    private val path: String = configuration.getOpaPath()

    @Synchronized
    fun getOpaResponse(
        subject: SubjectAttributes?,
        /* TODO: POST, GET, etc. Not yet implemented */
        action: GraphQLAuthorizationActionAttributes?,
        resource: GraphQLAuthorizationResourceAttributes?,
        /* TODO: Use context attributes as needed */
        context: ContextAttributes?,
        client: WebServiceClient
    ): AuthorizationResult<GraphQLObligation>
    {
        var resourceType: String = resource?.get("resourceType")?.getValueOfType(String::class.java).toString()

        /* OPA package name doesn't allow - converting the provided user-management -> um */
        if (resourceType == "user-management")
        {
            resourceType = "um"
        }

        val body = OpaRequestBody(
            subject?.get("group")?.getValueOfType(String::class.java),
            subject?.subject,
            resourceType
        )

        val opaHttpResponse: HttpResponse

        try
        {
            opaHttpResponse = client
                .withHost(host)
                .withPath("$path/$resourceType")
                .request()
                .timeout(Duration.ofSeconds(10))
                .contentType("application/json")
                .body(HttpRequest.fromJson(body, json))
                .post()
                .response()

            val statusCode = opaHttpResponse.statusCode()

            if (statusCode != 200)
            {
                if (logger.isInfoEnabled)
                {
                    logger.info("Got error response from OPA: error = {}, {}", statusCode,
                        opaHttpResponse.body(HttpResponse.asString()));
                }

                throw exceptionFactory.internalServerException(ErrorCode.EXTERNAL_SERVICE_ERROR);
            }
        }
        catch (e: HttpClient.HttpClientException)
        {
            throw exceptionFactory.externalServiceException("Unable to connect to OPA. Check the connection.");
        }

        val opaResponse: OpaResponse
        val opaObligation :GraphQLObligation

        try
        {
            opaResponse = opaHttpResponse.body(HttpResponse.asJsonObject(json)).toOpaResponse()
            opaObligation = if(opaResponse.unauthorizedFields?.isEmpty() == true)
            {
                OpaBeginOperationObligation(opaResponse)
            }
            else
            {
                OpaFilterResultObligation(opaResponse)
            }
        }
        catch (e: Exception) {
            logger.debug("Ann error occurred in handling the response from OPA. Returning deny.")
            return AuthorizationResult.deny("An error occurred in handling the response from OPA. Returning deny.")
        }
        return AuthorizationResult.allow(opaObligation)
    }

    @Throws(IOException::class)
    override fun close()
    {
        super.close()
    }
}