# AWS::CustomerProfiles::Integration Task

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#connectoroperator" title="ConnectorOperator">ConnectorOperator</a>" : <i><a href="connectoroperator.md">ConnectorOperator</a></i>,
    "<a href="#sourcefields" title="SourceFields">SourceFields</a>" : <i>[ String, ... ]</i>,
    "<a href="#destinationfield" title="DestinationField">DestinationField</a>" : <i>String</i>,
    "<a href="#tasktype" title="TaskType">TaskType</a>" : <i>String</i>,
    "<a href="#taskproperties" title="TaskProperties">TaskProperties</a>" : <i>[ <a href="taskpropertiesmap.md">TaskPropertiesMap</a>, ... ]</i>
}
</pre>

### YAML

<pre>
<a href="#connectoroperator" title="ConnectorOperator">ConnectorOperator</a>: <i><a href="connectoroperator.md">ConnectorOperator</a></i>
<a href="#sourcefields" title="SourceFields">SourceFields</a>: <i>
      - String</i>
<a href="#destinationfield" title="DestinationField">DestinationField</a>: <i>String</i>
<a href="#tasktype" title="TaskType">TaskType</a>: <i>String</i>
<a href="#taskproperties" title="TaskProperties">TaskProperties</a>: <i>
      - <a href="taskpropertiesmap.md">TaskPropertiesMap</a></i>
</pre>

## Properties

#### ConnectorOperator

_Required_: No

_Type_: <a href="connectoroperator.md">ConnectorOperator</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### SourceFields

_Required_: Yes

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### DestinationField

_Required_: No

_Type_: String

_Maximum Length_: <code>256</code>

_Pattern_: <code>.*</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### TaskType

_Required_: Yes

_Type_: String

_Allowed Values_: <code>Arithmetic</code> | <code>Filter</code> | <code>Map</code> | <code>Mask</code> | <code>Merge</code> | <code>Truncate</code> | <code>Validate</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### TaskProperties

_Required_: No

_Type_: List of <a href="taskpropertiesmap.md">TaskPropertiesMap</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

