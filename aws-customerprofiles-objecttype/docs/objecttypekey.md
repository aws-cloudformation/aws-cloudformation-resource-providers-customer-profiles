# AWS::CustomerProfiles::ObjectType ObjectTypeKey

An object that defines the Key element of a ProfileObject. A Key is a special element that can be used to search for a customer profile.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#fieldnames" title="FieldNames">FieldNames</a>" : <i>[ String, ... ]</i>,
    "<a href="#standardidentifiers" title="StandardIdentifiers">StandardIdentifiers</a>" : <i>[ String, ... ]</i>
}
</pre>

### YAML

<pre>
<a href="#fieldnames" title="FieldNames">FieldNames</a>: <i>
      - String</i>
<a href="#standardidentifiers" title="StandardIdentifiers">StandardIdentifiers</a>: <i>
      - String</i>
</pre>

## Properties

#### FieldNames

The reference for the key name of the fields map.

_Required_: No

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### StandardIdentifiers

The types of keys that a ProfileObject can have. Each ProfileObject can have only 1 UNIQUE key but multiple PROFILE keys. PROFILE means that this key can be used to tie an object to a PROFILE. UNIQUE means that it can be used to uniquely identify an object. If a key a is marked as SECONDARY, it will be used to search for profiles after all other PROFILE keys have been searched. A LOOKUP_ONLY key is only used to match a profile but is not persisted to be used for searching of the profile. A NEW_ONLY key is only used if the profile does not already exist before the object is ingested, otherwise it is only used for matching objects to profiles.

_Required_: No

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
