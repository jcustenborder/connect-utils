<#-- @formatter:off --><#include "common.rst.ftl">
<@section text=input.title/>

.. code-block:: text

    ${input.cls.name}

<#if input.description??>
${input.description}
</#if>

<@notes input=input/>

<@configExamples input=input />