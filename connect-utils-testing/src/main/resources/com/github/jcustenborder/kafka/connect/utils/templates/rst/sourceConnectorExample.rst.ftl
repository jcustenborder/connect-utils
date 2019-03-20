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

<#if input.outputJson??>
Data similar to the following will be emitted by this connector.

.. code-block:: json
    :caption: Output
    <#if input.outputEmphasizeLines?has_content>:emphasize-lines: ${input.outputEmphasizeLines?join(", ")}</#if>

${input.outputJson}
</#if>

