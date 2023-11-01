# AWS::CustomerProfiles::Domain

A domain defined for 3rd party data source in Profile Service

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::CustomerProfiles::Domain",
    "Properties" : {
        "<a href="#domainname" title="DomainName">DomainName</a>" : <i>String</i>,
        "<a href="#deadletterqueueurl" title="DeadLetterQueueUrl">DeadLetterQueueUrl</a>" : <i>String</i>,
        "<a href="#defaultencryptionkey" title="DefaultEncryptionKey">DefaultEncryptionKey</a>" : <i>String</i>,
        "<a href="#defaultexpirationdays" title="DefaultExpirationDays">DefaultExpirationDays</a>" : <i>Integer</i>,
        "<a href="#matching" title="Matching">Matching</a>" : <i><a href="matching.md">Matching</a></i>,
        "<a href="#rulebasedmatching" title="RuleBasedMatching">RuleBasedMatching</a>" : <i><a href="rulebasedmatching.md">RuleBasedMatching</a></i>,
        "<a href="#stats" title="Stats">Stats</a>" : <i><a href="domainstats.md">DomainStats</a></i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i>[ <a href="tag.md">Tag</a>, ... ]</i>,
    }
}
</pre>

### YAML

<pre>
Type: AWS::CustomerProfiles::Domain
Properties:
    <a href="#domainname" title="DomainName">DomainName</a>: <i>String</i>
    <a href="#deadletterqueueurl" title="DeadLetterQueueUrl">DeadLetterQueueUrl</a>: <i>String</i>
    <a href="#defaultencryptionkey" title="DefaultEncryptionKey">DefaultEncryptionKey</a>: <i>String</i>
    <a href="#defaultexpirationdays" title="DefaultExpirationDays">DefaultExpirationDays</a>: <i>Integer</i>
    <a href="#matching" title="Matching">Matching</a>: <i><a href="matching.md">Matching</a></i>
    <a href="#rulebasedmatching" title="RuleBasedMatching">RuleBasedMatching</a>: <i><a href="rulebasedmatching.md">RuleBasedMatching</a></i>
    <a href="#stats" title="Stats">Stats</a>: <i><a href="domainstats.md">DomainStats</a></i>
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

#### DeadLetterQueueUrl

The URL of the SQS dead letter queue

_Required_: No

_Type_: String

_Maximum Length_: <code>255</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### DefaultEncryptionKey

The default encryption key

_Required_: No

_Type_: String

_Maximum Length_: <code>255</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### DefaultExpirationDays

The default number of days until the data within the domain expires.

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Matching

The process of matching duplicate profiles. If Matching = true, Amazon Connect Customer Profiles starts a weekly batch process called Identity Resolution Job. If you do not specify a date and time for Identity Resolution Job to run, by default it runs every Saturday at 12AM UTC to detect duplicate profiles in your domains. After the Identity Resolution Job completes, use the GetMatches API to return and review the results. Or, if you have configured ExportingConfig in the MatchingRequest, you can download the results from S3.

_Required_: No

_Type_: <a href="matching.md">Matching</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### RuleBasedMatching

The process of matching duplicate profiles using the Rule-Based matching. If RuleBasedMatching = true, Amazon Connect Customer Profiles will start to match and merge your profiles according to your configuration in the RuleBasedMatchingRequest. You can use the ListRuleBasedMatches and GetSimilarProfiles API to return and review the results. Also, if you have configured ExportingConfig in the RuleBasedMatchingRequest, you can download the results from S3.

_Required_: No

_Type_: <a href="rulebasedmatching.md">RuleBasedMatching</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Stats

Usage-specific statistics about the domain.

_Required_: No

_Type_: <a href="domainstats.md">DomainStats</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tags

The tags (keys and values) associated with the domain

_Required_: No

_Type_: List of <a href="tag.md">Tag</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the DomainName.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### LastUpdatedAt

The time of this integration got last updated at

#### CreatedAt

The time of this integration got created

#### Status

Returns the <code>Status</code> value.

#### Stats

Usage-specific statistics about the domain.

