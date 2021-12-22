---
layout: default
title: CAS - Attribute Release Consent
category: Attributes
---

{% include variables.html %}

# Redis - Attribute Consent Storage

Support is enabled by including the following module in the WAR Overlay:

{% include_cached casmodule.html group="org.apereo.cas" module="cas-server-support-consent-redis" %}

## Configuration

{% include_cached casproperties.html properties="cas.consent.redis" %}