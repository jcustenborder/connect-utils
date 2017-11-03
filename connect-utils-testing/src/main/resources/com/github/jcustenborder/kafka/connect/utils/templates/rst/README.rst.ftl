<#-- @formatter:off --><#include "common.rst.ftl">
============
Introduction
============

<#if input.sourceConnectors?has_content>
=================
Source Connectors
=================

<#list input.sourceConnectors as source>
<@section text=source.title/>

<#if source.description??>
${source.description}
</#if>

<@notes input=source/>

<@configExamples input=source />
</#list>
</#if>

<#if input.sinkConnectors?has_content>
===============
Sink Connectors
===============

<#list input.sinkConnectors as sink>
<@section text=sink.title/>

<#if sink.description??>
${sink.description}
</#if>

<@notes input=sink/>

<@configExamples input=sink />
</#list>
</#if>

<#if input.transformations?has_content>
===============
Transformations
===============


<#list input.transformations as transformation>
<@section text=transformation.title/>

<#if transformation.description??>
${transformation.description}
</#if>

<@notes input=transformation/>

<@configExamples input=transformation />
</#list>

</#if>