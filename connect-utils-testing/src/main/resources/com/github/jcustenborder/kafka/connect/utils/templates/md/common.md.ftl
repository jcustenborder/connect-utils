<#-- @formatter:off --><#macro config connector>
### Configuration

<#if connector.configuration??>
<#list connector.configuration.groups as group>
#### ${group.name}

<#list group.items as item >

##### `${item.name}`

${item.doc}

*Importance:* ${item.importance}

*Type:* ${item.type}

<#if item.defaultValue?has_content>*Default Value:* ${item.defaultValue?string}

</#if>
<#if item.validator?has_content>*Validator:* ${item.validator}

</#if>

</#list>
</#list>
</#if>
</#macro>

<#macro banners connector>
<#if connector.danger??>
### Danger

${connector.danger}
</#if>
<#if connector.warning??>
### Warning

${connector.warning}
</#if>
<#if connector.important??>
### Important

${connector.important}
</#if>
<#if connector.note??>
### Note

${connector.note}
</#if>
<#if connector.tip??>
### Tip

${connector.tip}
</#if>
</#macro>

<#macro details connector>
## ${connector.title}

<#if connector.description??>
${connector.description}
</#if>

<@banners connector=connector/>

</#macro>

<#macro examples connector>

#### Examples

<#if connector.examples?has_content>
<#list connector.examples as example>

##### ${example.name}

${example.description}


Select one of the following configuration methods based on how you have deployed Kafka Connect.
Distributed Mode will the the JSON / REST examples. Standalone mode will use the properties based
example.

<#if example.type == "Connector">

###### Distributed Mode Json

```json
${example.markdownJson}
```

###### Standalone Mode Properties

```properties
${example.markdownProperties}
```

<#elseif example.type == "Transformation">

###### Distributed Mode Json

```json
${example.markdownJson}
```

###### Standalone Mode Properties

```properties
${example.markdownProperties}
```

<#elseif example.type == "Converter">
converter

</#if>

</#list>
<#else >

##### Standalone Example

This configuration is used typically along with [standalone mode](http://docs.confluent.io/current/connect/concepts.html#standalone-workers).

${markdownHelper.propertiesExample(connector)}

##### Distributed Example

This configuration is used typically along with [distributed mode](http://docs.confluent.io/current/connect/concepts.html#distributed-workers).
Write the following json to `connector.json`, configure all of the required values, and use the command below to
post the configuration to one the distributed connect worker(s).

${markdownHelper.jsonExample(connector)}

Use curl to post the configuration to one of the Kafka Connect Workers. Change `http://localhost:8083/` the the endpoint of
one of your Kafka Connect worker(s).

Create a new instance.
```bash
curl -s -X POST -H 'Content-Type: application/json' --data @connector.json http://localhost:8083/connectors
```

Update an existing instance.
```bash
curl -s -X PUT -H 'Content-Type: application/json' --data @connector.json http://localhost:8083/connectors/TestSinkConnector1/config
```
</#if>

</#macro>