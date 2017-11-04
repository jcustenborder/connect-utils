<#-- @formatter:off --><#include "common.md.ftl">
# Introduction

<#if input.sourceConnectors?has_content>
# Source Connectors

<#list input.sourceConnectors as source>
## ${source.title}

<#if source.description??>
${source.description}
</#if>

<@connectorConfig connector=source/>

</#list>
</#if>

<#if input.sinkConnectors?has_content>
# Sink Connectors

<#list input.sinkConnectors as sink>
## ${sink.title}

<#if sink.description??>
${sink.description}
</#if>

<@connectorConfig connector=sink/>

</#list>
</#if>

<#if input.transformations?has_content>
# Transformations

<#list input.transformations as transformation>
## ${transformation.title}

<#if transformation.description??>
${transformation.description}
</#if>

<@transformationConfig transformation=transformation/>

</#list>
</#if>