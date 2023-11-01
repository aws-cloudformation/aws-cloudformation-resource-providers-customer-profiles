# AWS::CustomerProfiles::Domain Matching

The process of matching duplicate profiles. If Matching = true, Amazon Connect Customer Profiles starts a weekly batch process called Identity Resolution Job. If you do not specify a date and time for Identity Resolution Job to run, by default it runs every Saturday at 12AM UTC to detect duplicate profiles in your domains. After the Identity Resolution Job completes, use the GetMatches API to return and review the results. Or, if you have configured ExportingConfig in the MatchingRequest, you can download the results from S3.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#enabled" title="Enabled">Enabled</a>" : <i>Boolean</i>,
    "<a href="#automerging" title="AutoMerging">AutoMerging</a>" : <i><a href="automerging.md">AutoMerging</a></i>,
    "<a href="#exportingconfig" title="ExportingConfig">ExportingConfig</a>" : <i><a href="exportingconfig.md">ExportingConfig</a></i>,
    "<a href="#jobschedule" title="JobSchedule">JobSchedule</a>" : <i><a href="jobschedule.md">JobSchedule</a></i>
}
</pre>

### YAML

<pre>
<a href="#enabled" title="Enabled">Enabled</a>: <i>Boolean</i>
<a href="#automerging" title="AutoMerging">AutoMerging</a>: <i><a href="automerging.md">AutoMerging</a></i>
<a href="#exportingconfig" title="ExportingConfig">ExportingConfig</a>: <i><a href="exportingconfig.md">ExportingConfig</a></i>
<a href="#jobschedule" title="JobSchedule">JobSchedule</a>: <i><a href="jobschedule.md">JobSchedule</a></i>
</pre>

## Properties

#### Enabled

The flag that enables the matching process of duplicate profiles.

_Required_: Yes

_Type_: Boolean

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### AutoMerging

Configuration information about the auto-merging process.

_Required_: No

_Type_: <a href="automerging.md">AutoMerging</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ExportingConfig

Configuration information for exporting Identity Resolution results, for example, to an S3 bucket.

_Required_: No

_Type_: <a href="exportingconfig.md">ExportingConfig</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### JobSchedule

The day and time when do you want to start the Identity Resolution Job every week.

_Required_: No

_Type_: <a href="jobschedule.md">JobSchedule</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

