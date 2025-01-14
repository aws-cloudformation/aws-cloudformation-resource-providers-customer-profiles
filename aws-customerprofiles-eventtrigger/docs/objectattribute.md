# AWS::CustomerProfiles::EventTrigger ObjectAttribute

The criteria that a specific object attribute must meet to trigger the destination.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#source" title="Source">Source</a>" : <i>String</i>,
    "<a href="#fieldname" title="FieldName">FieldName</a>" : <i>String</i>,
    "<a href="#comparisonoperator" title="ComparisonOperator">ComparisonOperator</a>" : <i>String</i>,
    "<a href="#values" title="Values">Values</a>" : <i>[ String, ... ]</i>
}
</pre>

### YAML

<pre>
<a href="#source" title="Source">Source</a>: <i>String</i>
<a href="#fieldname" title="FieldName">FieldName</a>: <i>String</i>
<a href="#comparisonoperator" title="ComparisonOperator">ComparisonOperator</a>: <i>String</i>
<a href="#values" title="Values">Values</a>: <i>
      - String</i>
</pre>

## Properties

#### Source

An attribute contained within a source object.

_Required_: No

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>1000</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### FieldName

A field defined within an object type.

_Required_: No

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>64</code>

_Pattern_: <code>^[a-zA-Z0-9_.-]+$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ComparisonOperator

The operator used to compare an attribute against a list of values.

_Required_: Yes

_Type_: String

_Allowed Values_: <code>INCLUSIVE</code> | <code>EXCLUSIVE</code> | <code>CONTAINS</code> | <code>BEGINS_WITH</code> | <code>ENDS_WITH</code> | <code>GREATER_THAN</code> | <code>LESS_THAN</code> | <code>GREATER_THAN_OR_EQUAL</code> | <code>LESS_THAN_OR_EQUAL</code> | <code>EQUAL</code> | <code>BEFORE</code> | <code>AFTER</code> | <code>ON</code> | <code>BETWEEN</code> | <code>NOT_BETWEEN</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Values

A list of attribute values used for comparison.

_Required_: Yes

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

