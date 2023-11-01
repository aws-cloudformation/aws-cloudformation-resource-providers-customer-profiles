# AWS::CustomerProfiles::ObjectType

An ObjectType resource of Amazon Connect Customer Profiles

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::CustomerProfiles::ObjectType",
    "Properties" : {
        "<a href="#domainname" title="DomainName">DomainName</a>" : <i>String</i>,
        "<a href="#objecttypename" title="ObjectTypeName">ObjectTypeName</a>" : <i>String</i>,
        "<a href="#allowprofilecreation" title="AllowProfileCreation">AllowProfileCreation</a>" : <i>Boolean</i>,
        "<a href="#description" title="Description">Description</a>" : <i>String</i>,
        "<a href="#encryptionkey" title="EncryptionKey">EncryptionKey</a>" : <i>String</i>,
        "<a href="#expirationdays" title="ExpirationDays">ExpirationDays</a>" : <i>Integer</i>,
        "<a href="#fields" title="Fields">Fields</a>" : <i>[ <a href="fieldmap.md">FieldMap</a>, ... ]</i>,
        "<a href="#keys" title="Keys">Keys</a>" : <i>[ <a href="keymap.md">KeyMap</a>, ... ]</i>,
        "<a href="#sourcelastupdatedtimestampformat" title="SourceLastUpdatedTimestampFormat">SourceLastUpdatedTimestampFormat</a>" : <i>String</i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>,
        "<a href="#templateid" title="TemplateId">TemplateId</a>" : <i>String</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::CustomerProfiles::ObjectType
Properties:
    <a href="#domainname" title="DomainName">DomainName</a>: <i>String</i>
    <a href="#objecttypename" title="ObjectTypeName">ObjectTypeName</a>: <i>String</i>
    <a href="#allowprofilecreation" title="AllowProfileCreation">AllowProfileCreation</a>: <i>Boolean</i>
    <a href="#description" title="Description">Description</a>: <i>String</i>
    <a href="#encryptionkey" title="EncryptionKey">EncryptionKey</a>: <i>String</i>
    <a href="#expirationdays" title="ExpirationDays">ExpirationDays</a>: <i>Integer</i>
    <a href="#fields" title="Fields">Fields</a>: <i>
      - <a href="fieldmap.md">FieldMap</a></i>
    <a href="#keys" title="Keys">Keys</a>: <i>
      - <a href="keymap.md">KeyMap</a></i>
    <a href="#sourcelastupdatedtimestampformat" title="SourceLastUpdatedTimestampFormat">SourceLastUpdatedTimestampFormat</a>: <i>String</i>
    <a href="#tags" title="Tags">Tags</a>: <i>
      - <a href="tag.md">Tag</a></i>
    <a href="#templateid" title="TemplateId">TemplateId</a>: <i>String</i>
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

#### ObjectTypeName

The name of the profile object type.

_Required_: No

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>255</code>

_Pattern_: <code>^[a-zA-Z_][a-zA-Z_0-9-]*$</code>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### AllowProfileCreation

Indicates whether a profile should be created when data is received.

_Required_: No

_Type_: Boolean

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Description

Description of the profile object type.

_Required_: No

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>1000</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### EncryptionKey

The default encryption key

_Required_: No

_Type_: String

_Maximum Length_: <code>255</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ExpirationDays

The default number of days until the data within the domain expires.

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Fields

A list of the name and ObjectType field.

_Required_: No

_Type_: List of <a href="fieldmap.md">FieldMap</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Keys

A list of unique keys that can be used to map data to the profile.

_Required_: No

_Type_: List of <a href="keymap.md">KeyMap</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### SourceLastUpdatedTimestampFormat

The format of your sourceLastUpdatedTimestamp that was previously set up.

_Required_: No

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>255</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tags

The tags (keys and values) associated with the integration.

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### TemplateId

A unique identifier for the object template.

_Required_: No

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>64</code>

_Pattern_: <code>^[a-zA-Z0-9_-]+$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### LastUpdatedAt

The time of this integration got last updated at.

#### CreatedAt

The time of this integration got created.
