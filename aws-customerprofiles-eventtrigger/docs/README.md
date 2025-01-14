# AWS::CustomerProfiles::EventTrigger

An event trigger resource of Amazon Connect Customer Profiles

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::CustomerProfiles::EventTrigger",
    "Properties" : {
        "<a href="#domainname" title="DomainName">DomainName</a>" : <i>String</i>,
        "<a href="#eventtriggername" title="EventTriggerName">EventTriggerName</a>" : <i>String</i>,
        "<a href="#objecttypename" title="ObjectTypeName">ObjectTypeName</a>" : <i>String</i>,
        "<a href="#description" title="Description">Description</a>" : <i>String</i>,
        "<a href="#eventtriggerconditions" title="EventTriggerConditions">EventTriggerConditions</a>" : <i>[ <a href="eventtriggercondition.md">EventTriggerCondition</a>, ... ]</i>,
        "<a href="#eventtriggerlimits" title="EventTriggerLimits">EventTriggerLimits</a>" : <i><a href="eventtriggerlimits.md">EventTriggerLimits</a></i>,
        "<a href="#segmentfilter" title="SegmentFilter">SegmentFilter</a>" : <i>String</i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::CustomerProfiles::EventTrigger
Properties:
    <a href="#domainname" title="DomainName">DomainName</a>: <i>String</i>
    <a href="#eventtriggername" title="EventTriggerName">EventTriggerName</a>: <i>String</i>
    <a href="#objecttypename" title="ObjectTypeName">ObjectTypeName</a>: <i>String</i>
    <a href="#description" title="Description">Description</a>: <i>String</i>
    <a href="#eventtriggerconditions" title="EventTriggerConditions">EventTriggerConditions</a>: <i>
      - <a href="eventtriggercondition.md">EventTriggerCondition</a></i>
    <a href="#eventtriggerlimits" title="EventTriggerLimits">EventTriggerLimits</a>: <i><a href="eventtriggerlimits.md">EventTriggerLimits</a></i>
    <a href="#segmentfilter" title="SegmentFilter">SegmentFilter</a>: <i>String</i>
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

#### EventTriggerName

The unique name of the event trigger.

_Required_: Yes

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>64</code>

_Pattern_: <code>^[a-zA-Z0-9_-]+$</code>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### ObjectTypeName

The unique name of the object type.

_Required_: Yes

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>255</code>

_Pattern_: <code>^[a-zA-Z_][a-zA-Z_0-9-]*$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Description

The description of the event trigger.

_Required_: No

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>1000</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### EventTriggerConditions

A list of conditions that determine when an event should trigger the destination.

_Required_: Yes

_Type_: List of <a href="eventtriggercondition.md">EventTriggerCondition</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### EventTriggerLimits

Defines limits controlling whether an event triggers the destination, based on ingestion latency and the number of invocations per profile over specific time periods.

_Required_: No

_Type_: <a href="eventtriggerlimits.md">EventTriggerLimits</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### SegmentFilter

The destination is triggered only for profiles that meet the criteria of a segment definition.

_Required_: No

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>64</code>

_Pattern_: <code>^[a-zA-Z0-9_-]+$</code>

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

The timestamp of when the event trigger was created.

#### LastUpdatedAt

The timestamp of when the event trigger was most recently updated.

