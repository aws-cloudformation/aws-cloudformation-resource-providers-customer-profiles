# AWS::CustomerProfiles::Domain AutoMerging

Configuration information about the auto-merging process.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#enabled" title="Enabled">Enabled</a>" : <i>Boolean</i>,
    "<a href="#conflictresolution" title="ConflictResolution">ConflictResolution</a>" : <i><a href="conflictresolution.md">ConflictResolution</a></i>,
    "<a href="#consolidation" title="Consolidation">Consolidation</a>" : <i><a href="consolidation.md">Consolidation</a></i>,
    "<a href="#minallowedconfidencescoreformerging" title="MinAllowedConfidenceScoreForMerging">MinAllowedConfidenceScoreForMerging</a>" : <i>Double</i>
}
</pre>

### YAML

<pre>
<a href="#enabled" title="Enabled">Enabled</a>: <i>Boolean</i>
<a href="#conflictresolution" title="ConflictResolution">ConflictResolution</a>: <i><a href="conflictresolution.md">ConflictResolution</a></i>
<a href="#consolidation" title="Consolidation">Consolidation</a>: <i><a href="consolidation.md">Consolidation</a></i>
<a href="#minallowedconfidencescoreformerging" title="MinAllowedConfidenceScoreForMerging">MinAllowedConfidenceScoreForMerging</a>: <i>Double</i>
</pre>

## Properties

#### Enabled

The flag that enables the auto-merging of duplicate profiles.

_Required_: Yes

_Type_: Boolean

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ConflictResolution

How the auto-merging process should resolve conflicts between different profiles. For example, if Profile A and Profile B have the same FirstName and LastName (and that is the matching criteria), which EmailAddress should be used?

_Required_: No

_Type_: <a href="conflictresolution.md">ConflictResolution</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Consolidation

A list of matching attributes that represent matching criteria. If two profiles meet at least one of the requirements in the matching attributes list, they will be merged.

_Required_: No

_Type_: <a href="consolidation.md">Consolidation</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### MinAllowedConfidenceScoreForMerging

A number between 0 and 1 that represents the minimum confidence score required for profiles within a matching group to be merged during the auto-merge process. A higher score means higher similarity required to merge profiles.

_Required_: No

_Type_: Double

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
