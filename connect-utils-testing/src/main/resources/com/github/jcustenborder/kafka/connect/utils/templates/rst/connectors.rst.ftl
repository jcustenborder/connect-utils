<#-- @formatter:off --><#include "common.rst.ftl">
<@section text="${input.title} Connectors"/>

<#if input.sourceConnectors?has_content>
.. toctree::
    :maxdepth: 1
    :caption: Source Connectors:
    :hidden:
    :glob:

    sources/*
</#if>

<#if input.sinkConnectors?has_content>
.. toctree::
    :maxdepth: 1
    :caption: Sink Connectors:
    :hidden:
    :glob:

    sinks/*
</#if>