# OPA Authorization Manager Plugin

[![Quality](https://img.shields.io/badge/quality-demo-red)](https://curity.io/resources/code-examples/status/)
[![Availability](https://img.shields.io/badge/availability-source-blue)](https://curity.io/resources/code-examples/status/)

A custom, Kotlin-based Authorization Manager plugin using OPA for authorization decisions.

Note: The plugin requires at least version 7.3 of the Curity Identity Server.

## Introduction
The Curity Identity Server can leverage Authorization Managers to control access to its exposed GraphQL APIs for User Management and DCR. This Authorization Manager leverage Open Policy Agent (OPA) as an external authorization engine to determine fine-grained access. OPA holds a policy and receives an authorization request from the plugin. The plugin handles the authorization response and controls access to the requested resource. The plugin makes use of an obligation filter in order to redact individual fields per the OPA policy.

## Building the Plugin

Build the plugin by issuing the command `mvn package`. This will produce a JAR file in the `target/opa-authorization-manager` directory, which can be installed.

## Installing the Plugin

To install the plugin, copy the compiled JAR from `target/opa-authorization-manager` into `${IDSVR_HOME}/usr/share/plugins/${pluginGroup}` on each node, including the admin node.

For more information about installing plugins, refer to the [Plugin Installation section of the Documentation](https://curity.io/docs/idsvr/latest/developer-guide/plugins/index.html#plugin-installation).

## Configuring the Plugin

| Name         | Type | Description                                                                                                                                                          | Example                 | Default         |
|--------------|------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------|-----------------|
| `HttpClient` | HttpClient | The HttpClient that the Authorization Manager will use to call OPA.                                                                                                  | `opa-http-client`       |                 |
| `OPA Host`   | String | The hostname of OPA.                                                                                                                                                 | `opa.example.com`       |                 |
| `OPA Port`   | String | The port that OPA is exposing its runtime service on.                                                                                                                 | `8443`                  | `8181`          |
| `OPA Path`   | String | The path where the appropriate policy of OPA is exposed. Note that by default the plugin adds `dcr` and `um` to this path depending on which GraphQL API is accessed. | `/v1/data/mynamespace/` | `/v1/data/curity/` |

### Create the Authorization Manager
Navigate to `System` -> `Authorization` and click `New Authorization`. Give it a name (`opa-authz-mngr` for example) and choose the type `Opa Authorization Manager`. Then provided the appropriate configurations for HttpClient, host, port, and path. Commit the changes.

### DCR GraphQL API

In order to protect the DCR GraphQL API, the Authorization Manager needs to be added to the Token Service Profile. Navigate to `Token Service` -> `General` and select the configured Authorization Manager (opa-authz-mngr) from the drop-down menu.

### User Management GraphQL API

In order to protect the User Management GraphQL API, the Authorization Manager needs to be added to the User Management Profile. Navigate to `User Management` -> `General` and select the configured Authorization Manager (opa-authz-mngr) from the drop-down menu.

## Testing

This repository contains a docker compose file that will run an instance of the Curity Identity Server, a data source with test data and an instance of [OPA](https://hub.docker.com/r/openpolicyagent/opa/). Running this environment will provide a fully configured environment that can be used to test the use cases and the plugin.

A script is available that will build and deploy the OPA Authorization Manager Plugin and start the docker containers. Run `/deploy.sh` to get everything up and running. Make sure to copy a valid license with the name `license.json` into `components/idsvr` before deploying. Run `./teardown.sh` to stop and remove all the containers.

1. Using for example cURL or [OAuth.tools](https://oauth.tools/), initiate a code flow using the `opa-demo` client (secret is `Password1`). This guide describes how to run the [Code Flow using cURL](https://curity.io/resources/learn/test-using-curl/) and this guide how to [run with OAuth.tools](https://curity.io/resources/learn/test-using-oauth-tools/). For both, remember to change the client ID from `www` -> `opa-demo` to match the configuration used in this example.
2. Log in with a user, `admin` or `demouser` (by default both have the password `Password1`). The `admin` user belongs to the group `admin` that has full access to the GraphQL APIs. The `demouser` belongs to the `devops` group that is subject to filtration of certain fields for both DCR and User Management data. Review the policy used by OPA to check which fields are filtered out for the devops group. Note that the group claim is issued by default per the configuration.
3. The access token that is obtained from running the code flow can be used in a call to either of the GraphQL APIs. Using for example Postman or Insomnia, construct a query and add the token in the `Authorization` header.

### Example User Query

```json
query getAccounts
{
   accounts(first: 5) {
    edges {
      node {
        id
        name {
          givenName
          middleName
          familyName
        }
        title
        active
        emails {
          value
          primary
        }
        phoneNumbers {
          value
          primary
        }
      }
    }
  }
}
```

### OPA Policies

The policies available in `components/opa/policies`.

#### DCR Policies

DCR policies are defined in `dcr-access.rego`. If the user has `group == "admin"`, all access is permitted. If the user instead belongs to the `devops` group, an attribute `unauthorized_fields` is returned that holds the names of the fields that the Authorization Manager will filter out from the response.

#### User Management Policies

Access to the User Management API is defined in `um-access.rego`. The structure is very similar to the DCR policies. Here, the `phoneNumbers` and `name` fields are filtered for users in the `devops` group as indicated by the `unauthorized_fields` attribute.

#### Sample Request/Response

To test OPA alone without the involvement of the Authorization Manager a sample request can be sent using for example Insomnia.

```json
POST /v1/data/curity/um HTTP/1.1
Host: localhost:8181
Content-Type: application/json
{
    "input": {
        "group": "devops",
      "resourceType": "um"
    }
}
```

This should return the response below from OPA that includes the attribute `unauthorized_fields` that in this case indicates what fields that the Authorization Manager should filter, i.e., `name` and `phoneNumbers`.

```json
{
   "result": {
      "allow": true,
      "unauthorized_fields": [
         "name",
         "phoneNumbers"
      ]
   }
}
```

## More Information

- Please visit [curity.io](https://curity.io/) for more information about the Curity Identity Server
- [Open Policy Agent](https://www.openpolicyagent.org/)
- [Curity Identity Server GraphQL APIs](https://curity.io/docs/idsvr/latest/developer-guide/graphql/index.html)
- [User Management with GraphQL](https://curity.io/resources/learn/graphql-user-management/)
- [Authorizing Access to User Data](https://curity.io/resources/learn/authorizing-user-access/)

Copyright (C) 2023 Curity AB.