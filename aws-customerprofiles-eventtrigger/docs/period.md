# AWS::CustomerProfiles::EventTrigger Period

Defines a limit and the time period during which it is enforced.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#unit" title="Unit">Unit</a>" : <i>String</i>,
    "<a href="#value" title="Value">Value</a>" : <i>Integer</i>,
    "<a href="#maxinvocationsperprofile" title="MaxInvocationsPerProfile">MaxInvocationsPerProfile</a>" : <i>Integer</i>,
    "<a href="#unlimited" title="Unlimited">Unlimited</a>" : <i>Boolean</i>
}
</pre>

### YAML

<pre>
<a href="#unit" title="Unit">Unit</a>: <i>String</i>
<a href="#value" title="Value">Value</a>: <i>Integer</i>
<a href="#maxinvocationsperprofile" title="MaxInvocationsPerProfile">MaxInvocationsPerProfile</a>: <i>Integer</i>
<a href="#unlimited" title="Unlimited">Unlimited</a>: <i>Boolean</i>
</pre>

## Properties

#### Unit

The unit of time.

_Required_: Yes

_Type_: String

_Allowed Values_: <code>HOURS</code> | <code>DAYS</code> | <code>WEEKS</code> | <code>MONTHS</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Value

The amount of time of the specified unit.

_Required_: Yes

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### MaxInvocationsPerProfile

The maximum allowed number of destination invocations per profile.

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Unlimited

If set to true, there is no limit on the number of destination invocations per profile. The default is false.

_Required_: No

_Type_: Boolean

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

