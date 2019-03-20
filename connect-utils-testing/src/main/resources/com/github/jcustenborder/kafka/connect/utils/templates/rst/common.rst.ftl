<#-- @formatter:off -->
<#macro section text>
<#list 0..<text?length as i>=</#list>
${text}
<#list 0..<text?length as i>=</#list>
</#macro>

<#macro subsection text>
<#list 0..<text?length as i>-</#list>
${text}
<#list 0..<text?length as i>-</#list>
</#macro>

<#macro subsubsection text>
<#list 0..<text?length as i>^</#list>
${text}
<#list 0..<text?length as i>^</#list>
</#macro>

<#macro configProperties connector>

This configuration is used typically along with `standalone mode
<http://docs.confluent.io/current/connect/concepts.html#standalone-workers>`_.

<#--${rstHelper.propertiesExample(connector)}-->


</#macro>

<#macro configJson connector>

This configuration is used typically along with `distributed mode
<http://docs.confluent.io/current/connect/concepts.html#distributed-workers>`_.
Write the following json to `connector.json`, configure all of the required values, and use the command below to
post the configuration to one the distributed connect worker(s). Check here for more information about the
`Kafka Connect REST Interface. <https://docs.confluent.io/current/connect/restapi.html>`_

<#--${rstHelper.jsonExample(connector)}-->

Use curl to post the configuration to one of the Kafka Connect Workers. Change `http://localhost:8083/` the the endpoint of
one of your Kafka Connect worker(s).

.. code-block:: bash
    :caption: Create a new connector

    curl -s -X POST -H 'Content-Type: application/json' --data @connector.json http://localhost:8083/connectors


.. code-block:: bash
    :caption: Update an existing connector

    curl -s -X PUT -H 'Content-Type: application/json' --data @connector.json http://localhost:8083/connectors/${connector.cls.simpleName}1/config


</#macro>

<#macro configuration configurable>
<#list configurable.configuration.groups as group>
<@subsection text = group.name />

<#list group.items as item>

<@subsubsection text=item.name/>

${item.doc}

**Importance:** ${item.importance}

**Type:** ${item.type}

<#if item.defaultValue?has_content>**Default Value:** ${item.defaultValue}

</#if>
<#if item.validator?has_content>**Validator:** ${item.validator}

</#if>

</#list>

</#list>


</#macro>

<#macro tableBar columnLengths column character="-"><#list 0..<columnLengths[column] as i>${character}</#list></#macro>
<#macro headerBar columnLengths column character="="><#list 0..<columnLengths[column] as i>${character}</#list></#macro>

<#macro tablePadText columnLengths column text><#assign pad=columnLengths[column] -1><#if text??> ${text?right_pad(pad)}<#else>${" "?right_pad(pad)}</#if></#macro>

<#macro configTable configs lengths>
+<@tableBar columnLengths=lengths column="name" />+<@tableBar columnLengths=lengths column="type" />+<@tableBar columnLengths=lengths column="importance" />+<@tableBar columnLengths=lengths column="defaultValue" />+<@tableBar columnLengths=lengths column="validator" />+<@tableBar columnLengths=lengths column="doc" />+
<#list configs as config>
|<@tablePadText columnLengths=lengths column="name" text=config.name() />|<@tablePadText columnLengths=lengths column="type" text=":ref:`configuration-${config.type()}`" />|<@tablePadText columnLengths=lengths column="importance" text=config.importance() />|<@tablePadText columnLengths=lengths column="defaultValue" text=config.defaultValue() />|<@tablePadText columnLengths=lengths column="validator" text=config.validator() />|<@tablePadText columnLengths=lengths column="doc" text=config.doc() />|
+<@tableBar columnLengths=lengths column="name" />+<@tableBar columnLengths=lengths column="type" />+<@tableBar columnLengths=lengths column="importance" />+<@tableBar columnLengths=lengths column="defaultValue" />+<@tableBar columnLengths=lengths column="validator" />+<@tableBar columnLengths=lengths column="doc" />+
</#list>
</#macro>

<#macro configExamples input>
    <#if input.examples?has_content>
.. toctree::
    :maxdepth: 1
    :caption: Examples:
    :glob:

    examples/${input.cls.simpleName}.*
    </#if>

<@configuration configurable=input/>
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
