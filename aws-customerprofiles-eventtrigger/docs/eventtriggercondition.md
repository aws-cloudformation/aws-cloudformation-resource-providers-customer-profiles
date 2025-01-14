# AWS::CustomerProfiles::EventTrigger EventTriggerCondition

Specifies the circumstances under which the event should trigger the destination.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#eventtriggerdimensions" title="EventTriggerDimensions">EventTriggerDimensions</a>" : <i>[ <a href="eventtriggerdimension.md">EventTriggerDimension</a>, ... ]</i>,
    "<a href="#logicaloperator" title="LogicalOperator">LogicalOperator</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#eventtriggerdimensions" title="EventTriggerDimensions">EventTriggerDimensions</a>: <i>
      - <a href="eventtriggerdimension.md">EventTriggerDimension</a></i>
<a href="#logicaloperator" title="LogicalOperator">LogicalOperator</a>: <i>String</i>
</pre>

## Properties

#### EventTriggerDimensions

A list of dimensions to be evaluated for the event.

_Required_: Yes

_Type_: List of <a href="eventtriggerdimension.md">EventTriggerDimension</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### LogicalOperator

The operator used to combine multiple dimensions.

_Required_: Yes

_Type_: String

_Allowed Values_: <code>ANY</code> | <code>ALL</code> | <code>NONE</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

