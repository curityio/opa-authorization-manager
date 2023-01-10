package com.example.curity.authorization

import se.curity.identityserver.sdk.authorization.GraphQLObligation
import se.curity.identityserver.sdk.authorization.ObligationDecisionResult

class OpaBeginOperationObligation(private val opaResponse: OpaResponse): GraphQLObligation.BeginOperation
{
    override fun canPerformOperation(input: GraphQLObligation.BeginOperation.Input): ObligationDecisionResult?
    {
        return if(opaResponse.allow)
            ObligationDecisionResult.allow()
        else return ObligationDecisionResult.deny("Not authorized to access resource")
    }
}
