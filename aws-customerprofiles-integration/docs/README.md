# AWS::CustomerProfiles::Integration

The resource schema for creating an Amazon Connect Customer Profiles Integration.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::CustomerProfiles::Integration",
    "Properties" : {
        "<a href="#domainname" title="DomainName">DomainName</a>" : <i>String</i>,
        "<a href="#uri" title="Uri">Uri</a>" : <i>String</i>,
        "<a href="#flowdefinition" title="FlowDefinition">FlowDefinition</a>" : <i><a href="flowdefinition.md">FlowDefinition</a></i>,
        "<a href="#objecttypename" title="ObjectTypeName">ObjectTypeName</a>" : <i>String</i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>,
        "<a href="#objecttypenames" title="ObjectTypeNames">ObjectTypeNames</a>" : <i>[ <a href="objecttypemapping.md">ObjectTypeMapping</a>, ... ]</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::CustomerProfiles::Integration
Properties:
    <a href="#domainname" title="DomainName">DomainName</a>: <i>String</i>
    <a href="#uri" title="Uri">Uri</a>: <i>String</i>
    <a href="#flowdefinition" title="FlowDefinition">FlowDefinition</a>: <i><a href="flowdefinition.md">FlowDefinition</a></i>
    <a href="#objecttypename" title="ObjectTypeName">ObjectTypeName</a>: <i>String</i>
    <a href="#tags" title="Tags">Tags</a>: <i>
      - <a href="tag.md">Tag</a></i>
    <a href="#objecttypenames" title="ObjectTypeNames">ObjectTypeNames</a>: <i>
      - <a href="objecttypemapping.md">ObjectTypeMapping</a></i>
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

#### Uri

The URI of the S3 bucket or any other type of data source.

_Required_: No

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>255</code>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### FlowDefinition

_Required_: No

_Type_: <a href="flowdefinition.md">FlowDefinition</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ObjectTypeName

The name of the ObjectType defined for the 3rd party data in Profile Service

_Required_: No

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>255</code>

_Pattern_: <code>^[a-zA-Z_][a-zA-Z_0-9-]*$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tags

The tags (keys and values) associated with the integration

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ObjectTypeNames

The mapping between 3rd party event types and ObjectType names

_Required_: No

_Type_: List of <a href="objecttypemapping.md">ObjectTypeMapping</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### LastUpdatedAt

The time of this integration got last updated at

#### CreatedAt

The time of this integration got created

