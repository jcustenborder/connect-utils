<#-- @formatter:off --><#include "common.rst.ftl">
.. _${input.schemaLink}:


<@section text=input.shortName />


<#if input.doc?has_content >
${input.doc}
</#if>

<#if input.type == "STRUCT">

<@subsection text="Fields"/>
${input.table}
</#if>