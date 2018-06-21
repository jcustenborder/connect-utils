<#-- @formatter:off -->
<#macro quickstart text>
Quickstart
----------

A simple demo of how to use the ${text} is available in the :ref:`Kafka Connect quick start guide <connect_quickstart>`.

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
.. danger::
    ${input.danger}


</#if><#if input.warning??>
.. warning::
    ${input.warning}


</#if><#if input.important??>
.. important::
    ${input.important}


</#if><#if input.tip??>
.. tip::
    ${input.tip}


</#if><#if input.note??>
.. note::
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

<#macro configProperties connector>

This configuration is used typically along with :ref:`standalone workers <standalone-workers>`.

${rstHelper.propertiesExample(connector)}


</#macro>

<#macro configJson connector>

This configuration is used typically along with :ref:`distributed workers <distributed-workers>`.
Write the following json to `connector.json`, configure all of the required values, and use the command below to
post the configuration to one the distributed connect worker(s). Check here for more information about the
Kafka Connect :ref:`Rest API <connect_userguide_rest>`

${rstHelper.jsonExample(connector)}

Use curl to post the configuration to one of the Kafka Connect Workers. Change `http://localhost:8083/` the the endpoint of
one of your Kafka Connect worker(s).

.. code-block:: bash
    :caption: Create a new connector

    curl -s -X POST -H 'Content-Type: application/json' --data @connector.json http://localhost:8083/connectors


.. code-block:: bash
    :caption: Update an existing connector

    curl -s -X PUT -H 'Content-Type: application/json' --data @connector.json http://localhost:8083/connectors/${connector.simpleName}1/config


</#macro>