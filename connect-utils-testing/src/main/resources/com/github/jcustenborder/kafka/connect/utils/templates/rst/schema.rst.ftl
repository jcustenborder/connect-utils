<#-- @formatter:off --><#include "common.rst.ftl">
<@section text=input.shortName />

<#if input.doc?has_content >
${input.doc}
</#if>

<#if input.type == "STRUCT">

<@subsection text="Fields"/>

<#list input.fields as field>

<@subsubsection text=field.name/>

<#if field.name?has_content >
${field.schema.doc}
</#if>

**Optional:** ${field.schema.required?string('yes', 'no')}

**Type:** ${field.schema.displayType}

<#if field.schema.defaultValue?has_content>**Default Value:** ${field.schema.defaultValue}

</#if>

</#list>

</#if>