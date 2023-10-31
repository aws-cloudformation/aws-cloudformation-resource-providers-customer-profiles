# AWS::CustomerProfiles::CalculatedAttributeDefinition Conditions

The conditions including range, object count, and threshold for the calculated attribute.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#range" title="Range">Range</a>" : <i><a href="range.md">Range</a></i>,
    "<a href="#objectcount" title="ObjectCount">ObjectCount</a>" : <i>Integer</i>,
    "<a href="#threshold" title="Threshold">Threshold</a>" : <i><a href="threshold.md">Threshold</a></i>
}
</pre>

### YAML

<pre>
<a href="#range" title="Range">Range</a>: <i><a href="range.md">Range</a></i>
<a href="#objectcount" title="ObjectCount">ObjectCount</a>: <i>Integer</i>
<a href="#threshold" title="Threshold">Threshold</a>: <i><a href="threshold.md">Threshold</a></i>
</pre>

## Properties

#### Range

The relative time period over which data is included in the aggregation.

_Required_: No

_Type_: <a href="range.md">Range</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ObjectCount

The number of profile objects used for the calculated attribute.

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Threshold

The threshold for the calculated attribute.

_Required_: No

_Type_: <a href="threshold.md">Threshold</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
