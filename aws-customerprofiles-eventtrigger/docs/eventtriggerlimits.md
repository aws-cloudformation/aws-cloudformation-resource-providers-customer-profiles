# AWS::CustomerProfiles::EventTrigger EventTriggerLimits

Defines limits controlling whether an event triggers the destination, based on ingestion latency and the number of invocations per profile over specific time periods.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#eventexpiration" title="EventExpiration">EventExpiration</a>" : <i>Integer</i>,
    "<a href="#periods" title="Periods">Periods</a>" : <i>[ <a href="period.md">Period</a>, ... ]</i>
}
</pre>

### YAML

<pre>
<a href="#eventexpiration" title="EventExpiration">EventExpiration</a>: <i>Integer</i>
<a href="#periods" title="Periods">Periods</a>: <i>
      - <a href="period.md">Period</a></i>
</pre>

## Properties

#### EventExpiration

Specifies that an event will only trigger the destination if it is processed within a certain latency period.

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Periods

A list of time periods during which the limits apply.

_Required_: No

_Type_: List of <a href="period.md">Period</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

