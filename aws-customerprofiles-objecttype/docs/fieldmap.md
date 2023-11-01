# AWS::CustomerProfiles::ObjectType FieldMap

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#name" title="Name">Name</a>" : <i>String</i>,
    "<a href="#objecttypefield" title="ObjectTypeField">ObjectTypeField</a>" : <i><a href="objecttypefield.md">ObjectTypeField</a></i>
}
</pre>

### YAML

<pre>
<a href="#name" title="Name">Name</a>: <i>String</i>
<a href="#objecttypefield" title="ObjectTypeField">ObjectTypeField</a>: <i><a href="objecttypefield.md">ObjectTypeField</a></i>
</pre>

## Properties

#### Name

_Required_: No

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>64</code>

_Pattern_: <code>^[a-zA-Z0-9_-]+$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ObjectTypeField

Represents a field in a ProfileObjectType.

_Required_: No

_Type_: <a href="objecttypefield.md">ObjectTypeField</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

