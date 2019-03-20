<#-- @formatter:off --><#include "common.rst.ftl">
<@section text=input.title/>

<#if input.keyValue>
.. code-block:: text
    :caption: Manipulates the key of the message.

    ${input.key.name}


.. code-block:: text
    :caption: Manipulates the value of the message.

    ${input.value.name}
<#else>
.. code-block:: text

    ${input.cls.name}
</#if>


<#if input.description??>
${input.description}
</#if>

<@notes input=input/>

<@configExamples input=input />
