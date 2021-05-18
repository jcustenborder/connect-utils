<#-- @formatter:off --><#include "common.rst.ftl">
<@section text=input.example.name/>

<#if input.example.description??>
${input.example.description}
</#if>

<@notes input=input.example/>

<@subsection text="Worker Configuration"/>

Add the following configuration to your Connect worker config file to enable this ConfigProvider to
be used by connectors.


<#if input.config??>
.. code-block:: properties
    :caption: Worker Configuration

${input.config}


</#if>




<@subsection text="Connector Configuration"/>

Add the following configurations to your connector configuration when you would like to retrieve
a value from the config provider

<#if input.connectorConfig??>
.. code-block:: properties
    :caption: Connector Configuration

${input.connectorConfig}


</#if>
