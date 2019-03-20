<#-- @formatter:off --><#include "common.rst.ftl">
<@section text=input.title/>

Installation through the `Confluent Hub Client <https://docs.confluent.io/current/connect/managing/confluent-hub/client.html>`_.

.. code-block:: json

    confluent-hub install ${input.pluginOwner}/${input.pluginName}:latest

${input.introduction}

<@notes input=input/>


<#if input.sinkConnectors?has_content || input.sourceConnectors?has_content>
.. toctree::
    :maxdepth: 1
    :caption: Connectors:
    :glob:

    <#if input.sinkConnectors?has_content>sinks/*</#if>
    <#if input.sourceConnectors?has_content>sources/*</#if>
</#if>


<#if input.transformations?has_content>
.. toctree::
    :maxdepth: 1
    :caption: Transformations:
    :glob:

    transformations/*
</#if>