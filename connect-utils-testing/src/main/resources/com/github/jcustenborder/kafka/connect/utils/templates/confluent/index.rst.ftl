<#-- @formatter:off --><#include "common.rst.ftl">
<@subsection text="Confluent ${input.title} Connector"/>

${input.introduction}

<@notes input=input/>


Contents:


.. toctree::
   :maxdepth: 3

<#if input.sourceConnectors?has_content><#list input.sourceConnectors as source>   ${source.confluentConnectorFileName}
   ${source.confluentConnectorConfigFileName}
   ${source.confluentConnectorExampleFileName}
</#list></#if>
<#if input.sinkConnectors?has_content><#list input.sinkConnectors as sink>   ${sink.confluentConnectorFileName}
   ${sink.confluentConnectorConfigFileName}
   ${sink.confluentConnectorExampleFileName}
</#list></#if>