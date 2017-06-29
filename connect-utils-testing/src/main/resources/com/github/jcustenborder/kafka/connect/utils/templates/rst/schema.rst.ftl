<#include "common.rst.ftl">
<#if input.name()?has_content >
    <@section text=input.name() />
</#if>

<#if input.doc()?has_content >
${input.doc()}
</#if>

<#macro schema input>
    <#switch input.type()>
        <#case "STRING">:ref:`schema-string`<#break>
        <#case "ARRAY">Array of <@schema input=input.valueSchema() /><#break>
        <#case "MAP">Map of <@schema input=input.keySchema() />, <@schema input=input.valueSchema() /><#break>
        <#case "BOOLEAN">:ref:`schema-boolean`<#break>
        <#case "BYTES">:ref:`schema-bytes`<#break>
        <#case "FLOAT32">:ref:`schema-float32`<#break>
        <#case "FLOAT64">:ref:`schema-float64`<#break>
        <#case "INT8">:ref:`schema-int8`<#break>
        <#case "INT16">:ref:`schema-int16`<#break>
        <#case "INT32">:ref:`schema-int32`<#break>
        <#case "INT64">:ref:`schema-int64`<#break>
        <#default>${input.type()}<#break>
    </#switch>
</#macro>

+<@tableBar columnLengths=lengths column="name" />+<@tableBar columnLengths=lengths column="optional" />+<@tableBar columnLengths=lengths column="schema" />+<@tableBar columnLengths=lengths column="defaultValue" />+<@tableBar columnLengths=lengths column="doc" />+
|<@tablePadText columnLengths=lengths column="name" text="Name" />|<@tablePadText columnLengths=lengths column="optional" text="Optional" />|<@tablePadText columnLengths=lengths column="schema" text="Schema" />|<@tablePadText columnLengths=lengths column="defaultValue" text="Default Value" />|<@tablePadText columnLengths=lengths column="doc" text="Documentation" />|
+<@headerBar columnLengths=lengths column="name" />+<@headerBar columnLengths=lengths column="optional" />+<@headerBar columnLengths=lengths column="schema" />+<@headerBar columnLengths=lengths column="defaultValue" />+<@headerBar columnLengths=lengths column="doc" />+
<#list input.fields() as field>
    <#assign doc><#if field.schema().doc()?has_content>${field.schema().doc()}<#else > </#if></#assign>
    <#assign defaultValue><#if field.schema().defaultValue()?has_content>${field.schema().defaultValue()}<#else > </#if></#assign>
    <#assign optional>${field.schema().isOptional()?c}</#assign>
    <#assign name>${field.name()}</#assign>
    <#assign schemaLink><@schema input=field.schema()/></#assign>
|<@tablePadText columnLengths=lengths column="name" text=name />|<@tablePadText columnLengths=lengths column="optional" text=optional />|<@tablePadText columnLengths=lengths column="schema" text=schemaLink />|<@tablePadText columnLengths=lengths column="defaultValue" text=defaultValue />|<@tablePadText columnLengths=lengths column="doc" text=doc />|
+<@tableBar columnLengths=lengths column="name" />+<@tableBar columnLengths=lengths column="optional" />+<@tableBar columnLengths=lengths column="schema" />+<@tableBar columnLengths=lengths column="defaultValue" />+<@tableBar columnLengths=lengths column="doc" />+
</#list>
