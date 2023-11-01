# AWS::CustomerProfiles::Domain RuleBasedMatching

The process of matching duplicate profiles using the Rule-Based matching. If RuleBasedMatching = true, Amazon Connect Customer Profiles will start to match and merge your profiles according to your configuration in the RuleBasedMatchingRequest. You can use the ListRuleBasedMatches and GetSimilarProfiles API to return and review the results. Also, if you have configured ExportingConfig in the RuleBasedMatchingRequest, you can download the results from S3.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#enabled" title="Enabled">Enabled</a>" : <i>Boolean</i>,
    "<a href="#attributetypesselector" title="AttributeTypesSelector">AttributeTypesSelector</a>" : <i><a href="attributetypesselector.md">AttributeTypesSelector</a></i>,
    "<a href="#conflictresolution" title="ConflictResolution">ConflictResolution</a>" : <i><a href="conflictresolution.md">ConflictResolution</a></i>,
    "<a href="#exportingconfig" title="ExportingConfig">ExportingConfig</a>" : <i><a href="exportingconfig.md">ExportingConfig</a></i>,
    "<a href="#matchingrules" title="MatchingRules">MatchingRules</a>" : <i>[ <a href="matchingrule.md">MatchingRule</a>, ... ]</i>,
    "<a href="#maxallowedrulelevelformatching" title="MaxAllowedRuleLevelForMatching">MaxAllowedRuleLevelForMatching</a>" : <i>Integer</i>,
    "<a href="#maxallowedrulelevelformerging" title="MaxAllowedRuleLevelForMerging">MaxAllowedRuleLevelForMerging</a>" : <i>Integer</i>,
}
</pre>

### YAML

<pre>
<a href="#enabled" title="Enabled">Enabled</a>: <i>Boolean</i>
<a href="#attributetypesselector" title="AttributeTypesSelector">AttributeTypesSelector</a>: <i><a href="attributetypesselector.md">AttributeTypesSelector</a></i>
<a href="#conflictresolution" title="ConflictResolution">ConflictResolution</a>: <i><a href="conflictresolution.md">ConflictResolution</a></i>
<a href="#exportingconfig" title="ExportingConfig">ExportingConfig</a>: <i><a href="exportingconfig.md">ExportingConfig</a></i>
<a href="#matchingrules" title="MatchingRules">MatchingRules</a>: <i>
      - <a href="matchingrule.md">MatchingRule</a></i>
<a href="#maxallowedrulelevelformatching" title="MaxAllowedRuleLevelForMatching">MaxAllowedRuleLevelForMatching</a>: <i>Integer</i>
<a href="#maxallowedrulelevelformerging" title="MaxAllowedRuleLevelForMerging">MaxAllowedRuleLevelForMerging</a>: <i>Integer</i>
</pre>

## Properties

#### Enabled

The flag that enables the rule-based matching process of duplicate profiles.

_Required_: Yes

_Type_: Boolean

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### AttributeTypesSelector

Configures information about the AttributeTypesSelector where the rule-based identity resolution uses to match profiles.

_Required_: No

_Type_: <a href="attributetypesselector.md">AttributeTypesSelector</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ConflictResolution

_Required_: No

_Type_: <a href="conflictresolution.md">ConflictResolution</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ExportingConfig

_Required_: No

_Type_: <a href="exportingconfig.md">ExportingConfig</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### MatchingRules

Configures how the rule-based matching process should match profiles. You can have up to 15 MatchingRule in the MatchingRules.

_Required_: No

_Type_: List of <a href="matchingrule.md">MatchingRule</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### MaxAllowedRuleLevelForMatching

Indicates the maximum allowed rule level for matching.

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### MaxAllowedRuleLevelForMerging

Indicates the maximum allowed rule level for merging.

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
