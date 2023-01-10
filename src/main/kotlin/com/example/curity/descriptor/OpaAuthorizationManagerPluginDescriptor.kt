package com.example.curity.descriptor

import com.example.curity.authorization.OpaAuthorizationManager
import com.example.curity.authorization.OpaAuthorizationManagerManagedObject
import com.example.curity.config.OpaAuthorizationManagerPluginConfig
import se.curity.identityserver.sdk.authorization.graphql.GraphQLAuthorizationManager
import se.curity.identityserver.sdk.plugin.descriptor.AuthorizationManagerPluginDescriptor
import java.util.Optional

class OpaAuthorizationManagerPluginDescriptor : AuthorizationManagerPluginDescriptor<OpaAuthorizationManagerPluginConfig>
{
    override fun getGraphQLAuthorizationManager(): Class<out GraphQLAuthorizationManager>
    {
        return OpaAuthorizationManager::class.java
    }

    override fun getConfigurationType(): Class<out OpaAuthorizationManagerPluginConfig> =
        OpaAuthorizationManagerPluginConfig::class.java

    override fun getPluginImplementationType(): String = "opa-authorization-manager"

    override fun createManagedObject(config: OpaAuthorizationManagerPluginConfig): Optional<OpaAuthorizationManagerManagedObject>
    {
        return Optional.of(OpaAuthorizationManagerManagedObject(config))
    }
}
