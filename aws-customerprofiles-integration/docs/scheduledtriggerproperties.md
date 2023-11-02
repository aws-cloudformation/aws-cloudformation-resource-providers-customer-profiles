# AWS::CustomerProfiles::Integration ScheduledTriggerProperties

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#scheduleexpression" title="ScheduleExpression">ScheduleExpression</a>" : <i>String</i>,
    "<a href="#datapullmode" title="DataPullMode">DataPullMode</a>" : <i>String</i>,
    "<a href="#schedulestarttime" title="ScheduleStartTime">ScheduleStartTime</a>" : <i>Double</i>,
    "<a href="#scheduleendtime" title="ScheduleEndTime">ScheduleEndTime</a>" : <i>Double</i>,
    "<a href="#timezone" title="Timezone">Timezone</a>" : <i>String</i>,
    "<a href="#scheduleoffset" title="ScheduleOffset">ScheduleOffset</a>" : <i>Integer</i>,
    "<a href="#firstexecutionfrom" title="FirstExecutionFrom">FirstExecutionFrom</a>" : <i>Double</i>
}
</pre>

### YAML

<pre>
<a href="#scheduleexpression" title="ScheduleExpression">ScheduleExpression</a>: <i>String</i>
<a href="#datapullmode" title="DataPullMode">DataPullMode</a>: <i>String</i>
<a href="#schedulestarttime" title="ScheduleStartTime">ScheduleStartTime</a>: <i>Double</i>
<a href="#scheduleendtime" title="ScheduleEndTime">ScheduleEndTime</a>: <i>Double</i>
<a href="#timezone" title="Timezone">Timezone</a>: <i>String</i>
<a href="#scheduleoffset" title="ScheduleOffset">ScheduleOffset</a>: <i>Integer</i>
<a href="#firstexecutionfrom" title="FirstExecutionFrom">FirstExecutionFrom</a>: <i>Double</i>
</pre>

## Properties

#### ScheduleExpression

_Required_: Yes

_Type_: String

_Maximum Length_: <code>256</code>

_Pattern_: <code>.*</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### DataPullMode

_Required_: No

_Type_: String

_Allowed Values_: <code>Incremental</code> | <code>Complete</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ScheduleStartTime

_Required_: No

_Type_: Double

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ScheduleEndTime

_Required_: No

_Type_: Double

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Timezone

_Required_: No

_Type_: String

_Maximum Length_: <code>256</code>

_Pattern_: <code>.*</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ScheduleOffset

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### FirstExecutionFrom

_Required_: No

_Type_: Double

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
