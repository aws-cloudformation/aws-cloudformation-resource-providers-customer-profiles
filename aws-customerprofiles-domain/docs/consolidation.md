# AWS::CustomerProfiles::Domain Consolidation

A list of matching attributes that represent matching criteria. If two profiles meet at least one of the requirements in the matching attributes list, they will be merged.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#matchingattributeslist" title="MatchingAttributesList">MatchingAttributesList</a>" : <i>[ [ String, ... ], ... ]</i>
}
</pre>

### YAML

<pre>
<a href="#matchingattributeslist" title="MatchingAttributesList">MatchingAttributesList</a>: <i>
      -
      - String</i>
</pre>

## Properties

#### MatchingAttributesList

A list of matching criteria.

_Required_: Yes

_Type_: List of List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
