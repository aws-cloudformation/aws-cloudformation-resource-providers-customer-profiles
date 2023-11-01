# AWS::CustomerProfiles::Integration SourceFlowConfig

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#connectortype" title="ConnectorType">ConnectorType</a>" : <i>String</i>,
    "<a href="#connectorprofilename" title="ConnectorProfileName">ConnectorProfileName</a>" : <i>String</i>,
    "<a href="#incrementalpullconfig" title="IncrementalPullConfig">IncrementalPullConfig</a>" : <i><a href="incrementalpullconfig.md">IncrementalPullConfig</a></i>,
    "<a href="#sourceconnectorproperties" title="SourceConnectorProperties">SourceConnectorProperties</a>" : <i><a href="sourceconnectorproperties.md">SourceConnectorProperties</a></i>
}
</pre>

### YAML

<pre>
<a href="#connectortype" title="ConnectorType">ConnectorType</a>: <i>String</i>
<a href="#connectorprofilename" title="ConnectorProfileName">ConnectorProfileName</a>: <i>String</i>
<a href="#incrementalpullconfig" title="IncrementalPullConfig">IncrementalPullConfig</a>: <i><a href="incrementalpullconfig.md">IncrementalPullConfig</a></i>
<a href="#sourceconnectorproperties" title="SourceConnectorProperties">SourceConnectorProperties</a>: <i><a href="sourceconnectorproperties.md">SourceConnectorProperties</a></i>
</pre>

## Properties

#### ConnectorType

_Required_: Yes

_Type_: String

_Allowed Values_: <code>Salesforce</code> | <code>Marketo</code> | <code>ServiceNow</code> | <code>Zendesk</code> | <code>S3</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ConnectorProfileName

_Required_: No

_Type_: String

_Maximum Length_: <code>256</code>

_Pattern_: <code>[\w/!@#+=.-]+</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### IncrementalPullConfig

_Required_: No

_Type_: <a href="incrementalpullconfig.md">IncrementalPullConfig</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### SourceConnectorProperties

_Required_: Yes

_Type_: <a href="sourceconnectorproperties.md">SourceConnectorProperties</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

