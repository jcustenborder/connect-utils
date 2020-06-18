<#-- @formatter:off --><#include "common.rst.ftl">
<@section text=input.title/>

${input.introduction}

<@notes input=input/>

.. toctree::
    :hidden:
    :maxdepth: 1
    :glob:

    sources/*


.. toctree::
    :hidden:
    :maxdepth: 1
    :glob:

    sinks/*


.. toctree::
    :hidden:
    :maxdepth: 1
    :glob:

    transformations/*


.. toctree::
    :hidden:
    :maxdepth: 1
    :glob:

    converters/*


.. toctree::
    :hidden:
    :maxdepth: 1

    schemas


<@subsection text="Installation"/>

The preferred method of installation is to utilize the `Confluent Hub Client <https://docs.confluent.io/current/connect/managing/confluent-hub/client.html>`_.

<@subsubsection text="Confluent Hub"/>
The plugin is hosted on the `Confluent Hub <https://www.confluent.io/hub/${input.pluginOwner}/${input.pluginName}>`_. Installation through the
`Confluent Hub Client <https://docs.confluent.io/current/connect/managing/confluent-hub/client.html>`_ is simple. Use the following command line.


.. code-block:: bash

    confluent-hub install ${input.pluginOwner}/${input.pluginName}:latest


<@subsubsection text="Manual Installation"/>

#. Compile the source code with `mvn clean package`
#. Create a subdirectory called `${input.pluginName}` under the `plugin.path` on your connect worker.
#. Extract the contents of the zip file from `target/components/packages/` to the directory you created in the previous step.
#. Restart the connect worker.
