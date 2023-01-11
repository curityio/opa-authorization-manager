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


