<#-- @formatter:off --><#include "common.rst.ftl">
<@section text=input.example.name/>

<#if input.example.description??>
${input.example.description}
</#if>

<@notes input=input.example/>

<#if input.config??>
.. code-block:: json
    :caption: Configuration

${input.config}
</#if>

<#if input.inputJson??>
.. code-block:: json
    :caption: Example record from Kafka.

${input.inputJson}
</#if>

<#if input.outputJson??>
The following data is written to the target.


.. code-block:: json
    :caption: Output

${input.outputJson}
</#if>

