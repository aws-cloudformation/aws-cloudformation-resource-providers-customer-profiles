# AWS::CustomerProfiles::Integration TriggerConfig

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#triggertype" title="TriggerType">TriggerType</a>" : <i>String</i>,
    "<a href="#triggerproperties" title="TriggerProperties">TriggerProperties</a>" : <i><a href="triggerproperties.md">TriggerProperties</a></i>
}
</pre>

### YAML

<pre>
<a href="#triggertype" title="TriggerType">TriggerType</a>: <i>String</i>
<a href="#triggerproperties" title="TriggerProperties">TriggerProperties</a>: <i><a href="triggerproperties.md">TriggerProperties</a></i>
</pre>

## Properties

#### TriggerType

_Required_: Yes

_Type_: String

_Allowed Values_: <code>Scheduled</code> | <code>Event</code> | <code>OnDemand</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### TriggerProperties

_Required_: No

_Type_: <a href="triggerproperties.md">TriggerProperties</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

