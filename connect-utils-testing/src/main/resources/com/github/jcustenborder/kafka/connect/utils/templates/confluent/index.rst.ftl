<#-- @formatter:off --><#include "common.rst.ftl">
<@subsection text="Confluent ${input.title} Connector"/>

<#if input.introduction??>${input.introduction}</#if>

<@notes input=input/>


Contents:


.. toctree::
   :maxdepth: 3

<#if input.sourceConnectors?has_content><#list input.sourceConnectors as source>   ${source.confluentConnectorFileName}
   ${source.confluentConnectorConfigFileName}
</#list></#if>
<#if input.sinkConnectors?has_content><#list input.sinkConnectors as sink>   ${sink.confluentConnectorFileName}
   ${sink.confluentConnectorConfigFileName}
</#list></#if>