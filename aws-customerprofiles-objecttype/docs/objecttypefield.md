# AWS::CustomerProfiles::ObjectType ObjectTypeField

Represents a field in a ProfileObjectType.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#source" title="Source">Source</a>" : <i>String</i>,
    "<a href="#target" title="Target">Target</a>" : <i>String</i>,
    "<a href="#contenttype" title="ContentType">ContentType</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#source" title="Source">Source</a>: <i>String</i>
<a href="#target" title="Target">Target</a>: <i>String</i>
<a href="#contenttype" title="ContentType">ContentType</a>: <i>String</i>
</pre>

## Properties

#### Source

A field of a ProfileObject. For example: _source.FirstName, where "_source" is a ProfileObjectType of a Zendesk user and "FirstName" is a field in that ObjectType.

_Required_: No

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>1000</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Target

The location of the data in the standard ProfileObject model. For example: _profile.Address.PostalCode.

_Required_: No

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>1000</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ContentType

The content type of the field. Used for determining equality when searching.

_Required_: No

_Type_: String

_Allowed Values_: <code>STRING</code> | <code>NUMBER</code> | <code>PHONE_NUMBER</code> | <code>EMAIL_ADDRESS</code> | <code>NAME</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

