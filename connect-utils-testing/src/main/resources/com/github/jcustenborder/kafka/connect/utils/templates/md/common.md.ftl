<#-- @formatter:off --><#macro config connector>
### Configuration

<#list connector.config.configs as item>
##### `${item.name}`
*Importance:* ${item.importance}

*Type:* ${item.type}

<#if item.defaultValue?has_content>*Default Value:* ${item.defaultValue}

</#if>
<#if item.validator?has_content>*Validator:* ${item.validator}

</#if>

${item.doc}
</#list>

#### Examples

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

</#macro>

<#macro details connector>
## ${connector.title}

<#if connector.description??>
${connector.description}
</#if>

<#if connector.danger??>
### Danger

${ connector.danger}
</#if>

<#if connector.warning??>
### Warning

${ connector.warning}
</#if>

<#if connector.important??>
### Important

${ connector.important}
</#if>

<#if connector.note??>
### Note

${ connector.note}
</#if>

<#if connector.tip??>
### Tip

${ connector.tip}
</#if>
</#macro>