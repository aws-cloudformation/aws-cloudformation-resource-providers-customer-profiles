# AWS::CustomerProfiles::Domain ConflictResolution

How the auto-merging process should resolve conflicts between different profiles. For example, if Profile A and Profile B have the same FirstName and LastName (and that is the matching criteria), which EmailAddress should be used? 

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#conflictresolvingmodel" title="ConflictResolvingModel">ConflictResolvingModel</a>" : <i>String</i>,
    "<a href="#sourcename" title="SourceName">SourceName</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#conflictresolvingmodel" title="ConflictResolvingModel">ConflictResolvingModel</a>: <i>String</i>
<a href="#sourcename" title="SourceName">SourceName</a>: <i>String</i>
</pre>

## Properties

#### ConflictResolvingModel

How the auto-merging process should resolve conflicts between different profiles.

_Required_: Yes

_Type_: String

_Allowed Values_: <code>RECENCY</code> | <code>SOURCE</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### SourceName

The ObjectType name that is used to resolve profile merging conflicts when choosing SOURCE as the ConflictResolvingModel.

_Required_: No

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>255</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

