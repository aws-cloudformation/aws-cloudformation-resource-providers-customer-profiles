# AWS::CustomerProfiles::CalculatedAttributeDefinition AttributeDetails

Mathematical expression and a list of attribute items specified in that expression.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#attributes" title="Attributes">Attributes</a>" : <i>[ <a href="attributeitem.md">AttributeItem</a>, ... ]</i>,
    "<a href="#expression" title="Expression">Expression</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#attributes" title="Attributes">Attributes</a>: <i>
      - <a href="attributeitem.md">AttributeItem</a></i>
<a href="#expression" title="Expression">Expression</a>: <i>String</i>
</pre>

## Properties

#### Attributes

A list of attribute items specified in the mathematical expression.

_Required_: Yes

_Type_: List of <a href="attributeitem.md">AttributeItem</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Expression

Mathematical expression that is performed on attribute items provided in the attribute list. Each element in the expression should follow the structure of "{ObjectTypeName.AttributeName}".

_Required_: Yes

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>255</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
