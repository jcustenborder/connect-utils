<#-- @formatter:off --><#include "common.rst.ftl">
<@section text="Confluent ${input.title} Connectors"/>

${input.introduction}

<@quickstart text=input.title/>

<#if input.sourceConnectors?has_content>
<#list input.sourceConnectors as source>
<@subsection text=source.title/>

<#if source.description??>
${source.description}
</#if>

<@notes input=source/>

</#list>
</#if>

<#if input.sinkConnectors?has_content>
<#list input.sinkConnectors as sink>
<@subsection text=sink.title/>

<#if sink.description??>
${sink.description}
</#if>

<@notes input=sink/>

</#list>
</#if>