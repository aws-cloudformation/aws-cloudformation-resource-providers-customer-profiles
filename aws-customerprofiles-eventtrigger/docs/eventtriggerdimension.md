# AWS::CustomerProfiles::EventTrigger EventTriggerDimension

A specific event dimension to be assessed.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#objectattributes" title="ObjectAttributes">ObjectAttributes</a>" : <i>[ <a href="objectattribute.md">ObjectAttribute</a>, ... ]</i>
}
</pre>

### YAML

<pre>
<a href="#objectattributes" title="ObjectAttributes">ObjectAttributes</a>: <i>
      - <a href="objectattribute.md">ObjectAttribute</a></i>
</pre>

## Properties

#### ObjectAttributes

A list of object attributes to be evaluated.

_Required_: Yes

_Type_: List of <a href="objectattribute.md">ObjectAttribute</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

