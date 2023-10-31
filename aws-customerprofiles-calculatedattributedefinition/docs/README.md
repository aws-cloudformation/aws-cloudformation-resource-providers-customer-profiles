# AWS::CustomerProfiles::CalculatedAttributeDefinition

A calculated attribute definition for Customer Profiles

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::CustomerProfiles::CalculatedAttributeDefinition",
    "Properties" : {
        "<a href="#domainname" title="DomainName">DomainName</a>" : <i>String</i>,
        "<a href="#calculatedattributename" title="CalculatedAttributeName">CalculatedAttributeName</a>" : <i>String</i>,
        "<a href="#displayname" title="DisplayName">DisplayName</a>" : <i>String</i>,
        "<a href="#description" title="Description">Description</a>" : <i>String</i>,
        "<a href="#attributedetails" title="AttributeDetails">AttributeDetails</a>" : <i><a href="attributedetails.md">AttributeDetails</a></i>,
        "<a href="#conditions" title="Conditions">Conditions</a>" : <i><a href="conditions.md">Conditions</a></i>,
        "<a href="#statistic" title="Statistic">Statistic</a>" : <i>String</i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::CustomerProfiles::CalculatedAttributeDefinition
Properties:
    <a href="#domainname" title="DomainName">DomainName</a>: <i>String</i>
    <a href="#calculatedattributename" title="CalculatedAttributeName">CalculatedAttributeName</a>: <i>String</i>
    <a href="#displayname" title="DisplayName">DisplayName</a>: <i>String</i>
    <a href="#description" title="Description">Description</a>: <i>String</i>
    <a href="#attributedetails" title="AttributeDetails">AttributeDetails</a>: <i><a href="attributedetails.md">AttributeDetails</a></i>
    <a href="#conditions" title="Conditions">Conditions</a>: <i><a href="conditions.md">Conditions</a></i>
    <a href="#statistic" title="Statistic">Statistic</a>: <i>String</i>
    <a href="#tags" title="Tags">Tags</a>: <i>
      - <a href="tag.md">Tag</a></i>
</pre>

## Properties

#### DomainName

The unique name of the domain.

_Required_: Yes

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>64</code>

_Pattern_: <code>^[a-zA-Z0-9_-]+$</code>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### CalculatedAttributeName

The unique name of the calculated attribute.

_Required_: Yes

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>255</code>

_Pattern_: <code>^[a-zA-Z_][a-zA-Z_0-9-]*$</code>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### DisplayName

The display name of the calculated attribute.

_Required_: No

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>255</code>

_Pattern_: <code>^[a-zA-Z_][a-zA-Z_0-9-\s]*$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Description

The description of the calculated attribute.

_Required_: No

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>1000</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### AttributeDetails

Mathematical expression and a list of attribute items specified in that expression.

_Required_: Yes

_Type_: <a href="attributedetails.md">AttributeDetails</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Conditions

The conditions including range, object count, and threshold for the calculated attribute.

_Required_: No

_Type_: <a href="conditions.md">Conditions</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Statistic

The aggregation operation to perform for the calculated attribute.

_Required_: Yes

_Type_: String

_Allowed Values_: <code>FIRST_OCCURRENCE</code> | <code>LAST_OCCURRENCE</code> | <code>COUNT</code> | <code>SUM</code> | <code>MINIMUM</code> | <code>MAXIMUM</code> | <code>AVERAGE</code> | <code>MAX_OCCURRENCE</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tags

An array of key-value pairs to apply to this resource.

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### CreatedAt

The timestamp of when the calculated attribute definition was created.

#### LastUpdatedAt

The timestamp of when the calculated attribute definition was most recently edited.
