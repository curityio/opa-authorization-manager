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
import java.util.*

class OpaAuthorizationManagerManagedObject(configuration: OpaAuthorizationManagerPluginConfig) :
    ManagedObject<OpaAuthorizationManagerPluginConfig?>(configuration) {
    private val json: Json = configuration.getJson()
    private val exceptionFactory: ExceptionFactory = configuration.getExceptionFactory()
    private val logger: Logger = LoggerFactory.getLogger(OpaAuthorizationManagerManagedObject::class.java)
    private val host: String = configuration.getOpaHost()
    private val path: String = configuration.getOpaPath()

    @Synchronized
    fun getOpaResponse(
        subject: SubjectAttributes?,
        action: GraphQLAuthorizationActionAttributes?,
        resource: GraphQLAuthorizationResourceAttributes?,
        context: ContextAttributes?,
        client: WebServiceClient
    ): AuthorizationResult<GraphQLObligation>
    {
        val resourceType: String = resource?.get("resourceType")?.getValueOfType(String::class.java).toString()

        val body = OpaRequestBody(
            subject?.get("groups")?.getValueOfType(String::class.java),
            subject?.subject,
            resourceType
            //TODO: Handle action (POST, GET, etc.)
            //TODO: Use context as needed
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

        val opaResponse = opaHttpResponse.body(HttpResponse.asJsonObject(json)).toOpaResponse()
        val opaObligation :GraphQLObligation = if(opaResponse.unauthorizedFields?.isEmpty() == true) {
            OpaBeginOperationObligation(opaResponse)
        } else{
            OpaFilterResultObligation(opaResponse)
        }

        return AuthorizationResult.allow(opaObligation)
    }

    @Throws(IOException::class)
    override fun close() {
        super.close()
    }
}

class OpaRequestBody(
    group: String?,
    subject: String?,
    resourceType: String?
) {
    val input = mapOf(
        Pair("groups", group),
        Pair("subject", subject),
        Pair("resourceType", resourceType)
    )
}

class OpaResponse(
    val allow: Boolean,
    val unauthorizedFields: ArrayList<String>?
)

fun Map<String, Any>.toOpaResponse(): OpaResponse {
    with(this["result"] as Map<String, *>) {
        return@toOpaResponse OpaResponse(
            this["allow"] as Boolean,
            this["unauthorized_fields"] as ArrayList<String>?
        )
    }
}