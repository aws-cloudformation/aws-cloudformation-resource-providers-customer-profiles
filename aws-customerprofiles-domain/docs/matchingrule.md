# AWS::CustomerProfiles::Domain MatchingRule

Specifies how does the rule-based matching process should match profiles.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#rule" title="Rule">Rule</a>" : <i>[ String, ... ]</i>
}
</pre>

### YAML

<pre>
<a href="#rule" title="Rule">Rule</a>: <i>
      - String</i>
</pre>

## Properties

#### Rule

A single rule level of the MatchRules. Configures how the rule-based matching process should match profiles.

_Required_: Yes

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
