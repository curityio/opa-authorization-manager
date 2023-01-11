package curity.um

import future.keywords.if

default allow := false

allow if {
    input.group == "admin"
    input.resourceType == "um"

} else {
	unauthorized_fields
} 

unauthorized_fields = {"phoneNumbers", "name"} {
	input.group == "devops"           
    input.resourceType == "um"
}
