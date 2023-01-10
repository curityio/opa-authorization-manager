package com.example.curity.authorization

import se.curity.identityserver.sdk.attribute.scim.v2.ResourceAttributes
import se.curity.identityserver.sdk.authorization.GraphQLObligation
import se.curity.identityserver.sdk.authorization.ObligationAlterationResult

class OpaFilterResultObligation(opaResponse: OpaResponse) : GraphQLObligation.CanReadAttributes
{

    private val fieldsToFilter = opaResponse.unauthorizedFields

    override fun filterReadAttributes(input: GraphQLObligation.CanReadAttributes.Input): ObligationAlterationResult<ResourceAttributes<*>>
    {
        var returnAttributes = input.resourceAttributes
        if (fieldsToFilter != null)
        {
            for (s in fieldsToFilter)
            {
                returnAttributes = returnAttributes.removeAttribute(s)
            }
        }
        return ObligationAlterationResult.of(returnAttributes)
    }
}
