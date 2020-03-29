---
layout: default
title: CAS - Service Authentication Policy
category: Services
---

# Service Authentication Policy

Each registered application in the registry may be assigned an authentication policy that indicates how CAS
should validate and execute the authentication transaction when processing the given service. The authentication policy
may at times override what is globally found in the CAS authentication engine, or it may present complementary features
to enhance the authentication flow.

```json
{
  "@class" : "org.apereo.cas.services.RegexRegisteredService",
  "serviceId" : "https://app.example.org/.+",
  "name" : "ExampleApp",
  "id" : 1,
  "authenticationPolicy" : {
    "@class" : "org.apereo.cas.services.DefaultRegisteredServiceAuthenticationPolicy",
    "requiredAuthenticationHandlers" : [ "handler2", "handler1" ],
    "criteria": {
      "@class": "org.apereo.cas.services.DefaultRegisteredServiceAuthenticationPolicyCriteria",
      "tryAll": true,
      "type": "ANY_AUTHENTICATION_HANDLER"
    }
  }
}
```    

The following fields may be assigned to the policy:

| Parameter             | Description
|-----------------------|-----------------------------------------------------------------------
| `requiredAuthenticationHandlers`  | A set of identifiers/names for the required authentication handlers available and configured in CAS. These names can be used to enforce a service definition to only use the authentication strategy carrying that name when an authentication request is submitted to CAS. While authentication methods in CAS all are given a default name, most if not all methods can be assigned a name via CAS settings.

## Authentication Policy Criteria

Authentication policy criteria can also be assigned to each application definition, which should override the global policy defined for the deployment.
Such policies should closely follow after those [that can be defined globally](../installation/Configuring-Authentication-Components.html#authentication-policy), 
with the following types:

| Type                  | Description
|-----------------------|-----------------------------------------------------------------------------------------------
| `DEFAULT`  | Use the globally defined policy and skip assigning a special override to this service definition.
| `ANY_AUTHENTICATION_HANDLER`  | Maps to the `Any` [authentication policy](../configuration/Configuration-Properties.html#authentication-policy).