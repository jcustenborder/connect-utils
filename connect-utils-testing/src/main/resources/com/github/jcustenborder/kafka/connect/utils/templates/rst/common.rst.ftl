<#-- @formatter:off -->
<#macro section text>
<#list 0..<text?length as i>=</#list>
${text}
<#list 0..<text?length as i>=</#list>
</#macro>

<#macro subsection text>
<#list 0..<text?length as i>-</#list>
${text}
<#list 0..<text?length as i>-</#list>
</#macro>

<#macro subsubsection text>
<#list 0..<text?length as i>^</#list>
${text}
<#list 0..<text?length as i>^</#list>
</#macro>

<#macro paragraph text>
<#list 0..<text?length as i>"</#list>
${text}
<#list 0..<text?length as i>"</#list>
</#macro>

<#macro configProperties connector>

This configuration is used typically along with `standalone mode
<http://docs.confluent.io/current/connect/concepts.html#standalone-workers>`_.

<#--${rstHelper.propertiesExample(connector)}-->


</#macro>

<#macro configuration configurable>
<@subsection text = "Configuration" />
<#list configurable.configuration.groups as group>
<@subsubsection text = group.name />

<#list group.items as item>

<@paragraph text=item.name/>

${item.doc}

**Importance:** ${item.importance}

**Type:** ${item.type}

<#if item.defaultValue?has_content>**Default Value:** ${item.defaultValue?string}

</#if>
<#if item.validator?has_content>**Validator:** ${item.validator}

</#if>

</#list>

</#list>


</#macro>

<#macro tableBar columnLengths column character="-"><#list 0..<columnLengths[column] as i>${character}</#list></#macro>
<#macro headerBar columnLengths column character="="><#list 0..<columnLengths[column] as i>${character}</#list></#macro>

<#macro tablePadText columnLengths column text><#assign pad=columnLengths[column] -1><#if text??> ${text?right_pad(pad)}<#else>${" "?right_pad(pad)}</#if></#macro>

<#macro configExamples input>
    <#if input.examples?has_content>
.. toctree::
    :maxdepth: 1
    :caption: Examples:
    :glob:

    examples/${input.cls.simpleName}.*
    </#if>

<@configuration configurable=input/>
</#macro>

<#macro notes input>

<#if input.danger??>
.. DANGER::
    ${input.danger}


</#if><#if input.warning??>
.. WARNING::
    ${input.warning}


</#if><#if input.important??>
.. IMPORTANT::
    ${input.important}


</#if><#if input.tip??>
.. TIP::
    ${input.tip}


</#if><#if input.note??>
.. NOTE::
    ${input.note}


</#if>
</#macro>
