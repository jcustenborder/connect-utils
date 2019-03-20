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
    :caption: Input
    <#if input.inputEmphasizeLines?has_content>:emphasize-lines: ${input.inputEmphasizeLines?join(", ")}</#if>

${input.inputJson}
</#if>

<#if input.outputJson??>
Change(s) in the output are emphasized if delta(s) are detected.

.. code-block:: json
    :caption: Output
    <#if input.outputEmphasizeLines?has_content>:emphasize-lines: ${input.outputEmphasizeLines?join(", ")}</#if>

${input.outputJson}
<#else >

**Record has been filtered by the transformation.**

</#if>

