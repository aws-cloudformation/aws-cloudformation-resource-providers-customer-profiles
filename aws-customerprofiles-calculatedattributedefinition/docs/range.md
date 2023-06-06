# AWS::CustomerProfiles::CalculatedAttributeDefinition Range

The relative time period over which data is included in the aggregation.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#value" title="Value">Value</a>" : <i>Integer</i>,
    "<a href="#unit" title="Unit">Unit</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#value" title="Value">Value</a>: <i>Integer</i>
<a href="#unit" title="Unit">Unit</a>: <i>String</i>
</pre>

## Properties

#### Value

The amount of time of the specified unit.

_Required_: Yes

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Unit

The unit of time.

_Required_: Yes

_Type_: String

_Allowed Values_: <code>DAYS</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

