<#-- @formatter:off --><#include "common.rst.ftl">
<@section text=input.title />

<#if input.schema.doc()?has_content >
${input.schema.doc()}
</#if>

${helper.table(input)}
