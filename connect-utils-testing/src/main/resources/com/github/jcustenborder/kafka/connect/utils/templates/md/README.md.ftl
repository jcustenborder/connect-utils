<#-- @formatter:off --><#include "common.md.ftl">
# Introduction
<#assign documentationRoot = "https://jcustenborder.github.io/kafka-connect-documentation/projects/${input.pluginName}">
[Documentation](${documentationRoot}) | [Download from the Confluent Hub](https://www.confluent.io/hub/${input.pluginOwner}/${input.pluginName})

<#if input.introduction?has_content>${input.introduction}</#if>

# Installation

## Confluent Hub

The following command can be used to install the plugin directly from the Confluent Hub using the
[Confluent Hub Client](https://docs.confluent.io/current/connect/managing/confluent-hub/client.html).

```bash
confluent-hub install ${input.pluginOwner}/${input.pluginName}:latest
```

## Manually

The zip file that is deployed to the [Confluent Hub](https://www.confluent.io/hub/${input.pluginOwner}/${input.pluginName}) is available under
`target/components/packages/`. You can manually extract this zip file which includes all dependencies. All the dependencies
that are required to deploy the plugin are under `target/kafka-connect-target` as well. Make sure that you include all the dependencies that are required
to run the plugin.

1. Create a directory under the `plugin.path` on your Connect worker.
2. Copy all of the dependencies under the newly created subdirectory.
3. Restart the Connect worker.

<#if input.configProviders?has_content>
# Converters
<#list input.configProviders as configProvider>
## [${configProvider.title}](${documentationRoot}/configProviders/${configProvider.cls.simpleName}.html)

```
${configProvider.cls.name}
```
<#if configProvider.description??>${configProvider.description}</#if>
<@banners connector=configProvider/>

</#list>
</#if>

<#if input.converters?has_content>
# Converters
    <#list input.converters as converter>
## [${converter.title}](${documentationRoot}/converters/${converter.cls.simpleName}.html)

```
${converter.cls.name}
```
<#if converter.description??>${converter.description}</#if>
<@banners connector=converter/>
<@config connector=converter/>
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

    </#list>
</#if>

# Development

## Building the source

```bash
mvn clean package
```

## Contributions

Contributions are always welcomed! Before you start any development please create an issue and
start a discussion. Create a pull request against your newly created issue and we're happy to see
if we can merge your pull request. First and foremost any time you're adding code to the code base
you need to include test coverage. Make sure that you run `mvn clean package` before submitting your
pull to ensure that all of the tests, checkstyle rules, and the package can be successfully built.