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
