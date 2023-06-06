# AWS::CustomerProfiles::CalculatedAttributeDefinition Threshold

The threshold for the calculated attribute.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#value" title="Value">Value</a>" : <i>String</i>,
    "<a href="#operator" title="Operator">Operator</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#value" title="Value">Value</a>: <i>String</i>
<a href="#operator" title="Operator">Operator</a>: <i>String</i>
</pre>

## Properties

#### Value

The value of the threshold.

_Required_: Yes

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>255</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Operator

The operator of the threshold.

_Required_: Yes

_Type_: String

_Allowed Values_: <code>EQUAL_TO</code> | <code>GREATER_THAN</code> | <code>LESS_THAN</code> | <code>NOT_EQUAL_TO</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

