<#-- @formatter:off -->
<#macro quickstart text>
Quickstart
----------

A simple demo of how to use the ${text} is
available in the :ref:`Kafka Connect quick start guide <connect_quickstart>`.

</#macro>
<#macro section text>
${text}
<#list 0..<text?length as i>=</#list>
</#macro>

<#macro subsection text>
${text}
<#list 0..<text?length as i>-</#list>
</#macro>

<#macro subsubsection text>
${text}
<#list 0..<text?length as i>^</#list>
</#macro>

<#macro notes input>

<#if input.danger??>
.. DANGER::
    ${input.danger}


</#if><#if input.warning??>
.. WARNING::
    ${input.warning}


</#if><#if input.important??>
.. IMPORTANT::
    ${input.important}


</#if><#if input.tip??>
.. TIP::
    ${input.tip}


</#if><#if input.note??>
.. NOTE::
    ${input.note}


</#if>
</#macro>

<#macro connectorTag connector>
.. ${connector.rstConnectorTag}:

</#macro>

<#macro connectorConfigTag connector>
.. ${connector.rstConnectorConfigTag}:

</#macro>

<#macro connectorExampleTag connector>
.. ${connector.rstConnectorExamplesTag}:

</#macro>

<#macro configuration configurable>
<#list configurable.config.groups as group>
<@subsubsection text = group.name />

<#list group.items as item>

``${item.name}``
${item.doc}

  * Type: ${item.type}
  * Importance: ${item.importance}
<#if item.defaultValue?has_content>  * Default Value: ${item.defaultValue}</#if>
<#if item.validator?has_content>  * Valid Values: ${item.validator}</#if>
</#list>

</#list>


</#macro>


<#macro configExamples input>

<#if input.examples?has_content>
<#list input.examples as example>
<@subsubsection text="${example.name}" />

${example.description}

<@notes input=example />

Select one of the following configuration methods based on how you have deployed Kafka Connect.
Distributed Mode will the the JSON / REST examples. Standalone mode will use the properties based
example.

<#if example.type == "Connector">

**Distributed Mode Json**

${rstHelper.jsonExample(example)}

**Standalone Mode Properties**

${rstHelper.propertiesExample(example)}

<#elseif example.type == "Transformation">

**Distributed Mode Json**

${rstHelper.jsonExample(example)}

**Standalone Mode Properties**

${rstHelper.propertiesExample(example)}

<#elseif example.type == "Converter">
converter

</#if>

</#list>
<#else >
<@subsubsection text="Property based example" />

<@configProperties connector=input />

<@subsubsection text="Rest based example" />

<@configJson connector=input />
</#if>

</#macro>