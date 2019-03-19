<#-- @formatter:off --><#include "common.md.ftl">
# Introduction
<#assign documentationRoot = "https://jcustenborder.github.io/kafka-connect-documentation/projects/${input.pluginName}">
[Documentation](${documentationRoot})

Installation through the [Confluent Hub Client](https://docs.confluent.io/current/connect/managing/confluent-hub/client.html)

<#if input.introduction?has_content>${input.introduction}</#if>

<#if input.sourceConnectors?has_content>
    <#list input.sourceConnectors as connector>
## [${connector.title}](${documentationRoot}/sources/${connector.cls.simpleName}.html)

<#if connector.description??>${connector.description}</#if>
    </#list>
</#if>

<#if input.sinkConnectors?has_content>
    <#list input.sinkConnectors as connector>
## [${connector.title}](${documentationRoot}/sinks/${connector.cls.simpleName}.html)

<#if connector.description??>${connector.description}</#if>
    </#list>
</#if>

<#if input.transformations?has_content>
    <#list input.transformations as transformation>
## [${transformation.title}](${documentationRoot}/transformations/${transformation.cls.simpleName}.html)

<#if transformation.description??>${transformation.description}</#if>
    </#list>
</#if>

# Development

## Building the source

```bash
mvn clean package
```