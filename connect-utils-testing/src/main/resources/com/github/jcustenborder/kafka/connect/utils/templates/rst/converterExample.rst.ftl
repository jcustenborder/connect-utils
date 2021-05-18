<#-- @formatter:off --><#include "common.rst.ftl">
<@section text=input.example.name/>

<#if input.example.description??>
${input.example.description}
</#if>

<@notes input=input.example/>

<@subsection text="Worker Configuration"/>

Add the following configurations to your Connect worker configuration if you would like use this
converter by default for connectors that do not specify a `key.converter` or a `value.converter`.


<#if input.workerKeyConfig??>
.. code-block:: properties
    :caption: Worker Key Converter Configuration

${input.workerKeyConfig}


</#if>


<#if input.workerValueConfig??>
.. code-block:: properties
    :caption: Worker Value Converter Configuration

${input.workerValueConfig}


</#if>


<@subsection text="Connector Configuration"/>

Add the following configurations to your connector configuration if you would like use this
converter on a specific connector.


<#if input.connectorKeyConfig??>
.. code-block:: properties
    :caption: Connector Key Converter Configuration

${input.connectorKeyConfig}


</#if>


<#if input.connectorValueConfig??>
.. code-block:: properties
    :caption: Connector Value Converter Configuration

${input.connectorValueConfig}


</#if>