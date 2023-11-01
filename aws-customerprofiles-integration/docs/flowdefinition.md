# AWS::CustomerProfiles::Integration FlowDefinition

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#flowname" title="FlowName">FlowName</a>" : <i>String</i>,
    "<a href="#description" title="Description">Description</a>" : <i>String</i>,
    "<a href="#kmsarn" title="KmsArn">KmsArn</a>" : <i>String</i>,
    "<a href="#tasks" title="Tasks">Tasks</a>" : <i>[ <a href="task.md">Task</a>, ... ]</i>,
    "<a href="#triggerconfig" title="TriggerConfig">TriggerConfig</a>" : <i><a href="triggerconfig.md">TriggerConfig</a></i>,
    "<a href="#sourceflowconfig" title="SourceFlowConfig">SourceFlowConfig</a>" : <i><a href="sourceflowconfig.md">SourceFlowConfig</a></i>
}
</pre>

### YAML

<pre>
<a href="#flowname" title="FlowName">FlowName</a>: <i>String</i>
<a href="#description" title="Description">Description</a>: <i>String</i>
<a href="#kmsarn" title="KmsArn">KmsArn</a>: <i>String</i>
<a href="#tasks" title="Tasks">Tasks</a>: <i>
      - <a href="task.md">Task</a></i>
<a href="#triggerconfig" title="TriggerConfig">TriggerConfig</a>: <i><a href="triggerconfig.md">TriggerConfig</a></i>
<a href="#sourceflowconfig" title="SourceFlowConfig">SourceFlowConfig</a>: <i><a href="sourceflowconfig.md">SourceFlowConfig</a></i>
</pre>

## Properties

#### FlowName

_Required_: Yes

_Type_: String

_Maximum Length_: <code>256</code>

_Pattern_: <code>[a-zA-Z0-9][\w!@#.-]+</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Description

_Required_: No

_Type_: String

_Maximum Length_: <code>2048</code>

_Pattern_: <code>[\w!@#\-.?,\s]*</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### KmsArn

_Required_: Yes

_Type_: String

_Minimum Length_: <code>20</code>

_Maximum Length_: <code>2048</code>

_Pattern_: <code>arn:aws:kms:.*:[0-9]+:.*</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tasks

_Required_: Yes

_Type_: List of <a href="task.md">Task</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### TriggerConfig

_Required_: Yes

_Type_: <a href="triggerconfig.md">TriggerConfig</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### SourceFlowConfig

_Required_: Yes

_Type_: <a href="sourceflowconfig.md">SourceFlowConfig</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
