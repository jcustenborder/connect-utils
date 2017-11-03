<#macro connectorConfig connector>
### Configuration

${markdownHelper.table(connector.config)}

#### Standalone Example

```properties
name=connector1
tasks.max=1
connector.class=${connector.className}
# The following values must be configured.
    <#list connector.config.requiredConfigs as item>
    ${item.name()}=
    </#list>
```

#### Distributed Example

```json
{
"name": "connector1",
"config": {
"connector.class": "${connector.className}",
    <#list connector.config.requiredConfigs as item>
    "${item.name()}":"",
    </#list>
}
}
```
</#macro>

<#macro transformationConfig transformation>
### Configuration

${markdownHelper.table(transformation.config)}

#### Standalone Example

```properties
transforms=${transformation.simpleName}
transforms.${transformation.simpleName}.type=${transformation.className}
# The following values must be configured.
    <#list transformation.config.requiredConfigs as item>
    transforms.${transformation.simpleName}.${item.name()}=
    </#list>
```

#### Distributed Example

```json
{
"name": "connector1",
"config": {
"connector.class": "${transformation.className}",
"transforms": "${transformation.simpleName}",
"transforms.${transformation.simpleName}.type": "${transformation.className}",
    <#list transformation.config.requiredConfigs as item>
    "transforms.${transformation.simpleName}.${item.name()}":"",
    </#list>
}
}
```
</#macro>