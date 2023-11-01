# AWS::CustomerProfiles::Domain JobSchedule

The day and time when do you want to start the Identity Resolution Job every week.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#dayoftheweek" title="DayOfTheWeek">DayOfTheWeek</a>" : <i>String</i>,
    "<a href="#time" title="Time">Time</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#dayoftheweek" title="DayOfTheWeek">DayOfTheWeek</a>: <i>String</i>
<a href="#time" title="Time">Time</a>: <i>String</i>
</pre>

## Properties

#### DayOfTheWeek

The day when the Identity Resolution Job should run every week.

_Required_: Yes

_Type_: String

_Allowed Values_: <code>SUNDAY</code> | <code>MONDAY</code> | <code>TUESDAY</code> | <code>WEDNESDAY</code> | <code>THURSDAY</code> | <code>FRIDAY</code> | <code>SATURDAY</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Time

The time when the Identity Resolution Job should run every week.

_Required_: Yes

_Type_: String

_Minimum Length_: <code>3</code>

_Maximum Length_: <code>5</code>

_Pattern_: <code>^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

