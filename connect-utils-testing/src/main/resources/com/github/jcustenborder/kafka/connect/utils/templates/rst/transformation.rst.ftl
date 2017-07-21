<#-- @formatter:off --><#include "common.rst.ftl">
<@section text=input.title/>

<#if input.description??>
${input.description}
</#if>


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


</#if>

<@configExamples input=input />