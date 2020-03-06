<#-- @formatter:off --><#include "common.md.ftl">
# Introduction
<#assign documentationRoot = "https://jcustenborder.github.io/kafka-connect-documentation/projects/${input.pluginName}">
[Documentation](${documentationRoot})

Installation through the [Confluent Hub Client](https://docs.confluent.io/current/connect/managing/confluent-hub/client.html)

<#if input.introduction?has_content>${input.introduction}</#if>

<#if input.sourceConnectors?has_content>
# Converters
    <#list input.converters as converter>
## [${converter.title}](${documentationRoot}/sources/${converter.cls.simpleName}.html)

```
${converter.cls.name}
```
<#if converter.description??>${converter.description}</#if>
    </#list>
</#if>

<#if input.sourceConnectors?has_content>
# Source Connectors
    <#list input.sourceConnectors as connector>
## [${connector.title}](${documentationRoot}/sources/${connector.cls.simpleName}.html)

```
${connector.cls.name}
```

<#if connector.description??>${connector.description}</#if>
<@banners connector=connector/>
<@config connector=connector/>
    </#list>
</#if>

<#if input.sinkConnectors?has_content>
# Sink Connectors
    <#list input.sinkConnectors as connector>
## [${connector.title}](${documentationRoot}/sinks/${connector.cls.simpleName}.html)

```
${connector.cls.name}
```

<#if connector.description??>${connector.description}</#if>
<@banners connector=connector/>
<@config connector=connector/>
    </#list>
</#if>

<#if input.transformations?has_content>
# Transformations
    <#list input.transformations as transformation>
## [${transformation.title}](${documentationRoot}/transformations/${transformation.cls.simpleName}.html)

<#if transformation.keyValue>
*Key*
```
${transformation.key.name}
```
*Value*
```
${transformation.value.name}
```
<#else>
```
${transformation.cls.name}
```
</#if>

<#if transformation.description??>${transformation.description}</#if>
<@banners connector=transformation/>
<@config connector=transformation/>

    </#list>
</#if>

# Development

## Building the source

```bash
mvn clean package
```