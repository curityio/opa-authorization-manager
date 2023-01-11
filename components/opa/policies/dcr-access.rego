package curity.dcr

import future.keywords.if

default allow := false

allow if {
    input.group == "admin"
    input.resourceType == "dcr"

} else {
	unauthorized_fields
} 

unauthorized_fields = {"authenticated_user"} {
	input.group == "devops"           
    input.resourceType == "dcr"
}
