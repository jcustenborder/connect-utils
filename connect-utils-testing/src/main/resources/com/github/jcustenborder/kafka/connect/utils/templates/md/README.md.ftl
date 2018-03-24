<#-- @formatter:off --><#include "common.md.ftl">
# Introduction

<#if input.sourceConnectors?has_content>
# Source Connectors

<#list input.sourceConnectors as connector>

<@details connector=connector/>

<@config connector=connector/>

</#list>
</#if>

<#if input.sinkConnectors?has_content>
# Sink Connectors

<#list input.sinkConnectors as connector>

<@details connector=connector/>

<@config connector=connector/>

</#list>
</#if>

<#if input.transformations?has_content>
# Transformations

<#list input.transformations as transformation>

<@details connector=transformation/>

<@config connector=transformation/>

</#list>
</#if>