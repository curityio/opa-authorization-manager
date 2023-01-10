package com.example.curity.authorization

import com.example.curity.config.OpaAuthorizationManagerPluginConfig
import se.curity.identityserver.sdk.attribute.ContextAttributes
import se.curity.identityserver.sdk.attribute.SubjectAttributes
import se.curity.identityserver.sdk.authorization.AuthorizationResult
import se.curity.identityserver.sdk.authorization.GraphQLObligation
import se.curity.identityserver.sdk.authorization.graphql.GraphQLAuthorizationActionAttributes
import se.curity.identityserver.sdk.authorization.graphql.GraphQLAuthorizationManager
import se.curity.identityserver.sdk.authorization.graphql.GraphQLAuthorizationResourceAttributes
import se.curity.identityserver.sdk.service.WebServiceClientFactory

class OpaAuthorizationManager(private val configuration: OpaAuthorizationManagerPluginConfig, private val opaManagedObject: OpaAuthorizationManagerManagedObject): GraphQLAuthorizationManager
{
    private val webServiceClientFactory: WebServiceClientFactory = configuration.getWebServiceClientFactory()

    override fun getGraphQLAuthorizationResult(
        subject: SubjectAttributes?,
        action: GraphQLAuthorizationActionAttributes?,
        resource: GraphQLAuthorizationResourceAttributes?,
        context: ContextAttributes?
    ): AuthorizationResult<GraphQLObligation>
    {
        return opaManagedObject.getOpaResponse(
            subject,
            action,
            resource,
            context,
            webServiceClientFactory.create(
                configuration.getHttpClient(),
                configuration.getOpaPort()))
    }
}


