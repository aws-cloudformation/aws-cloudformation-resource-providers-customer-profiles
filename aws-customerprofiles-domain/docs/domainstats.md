# AWS::CustomerProfiles::Domain DomainStats

Usage-specific statistics about the domain.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#meteringprofilecount" title="MeteringProfileCount">MeteringProfileCount</a>" : <i>Double</i>,
    "<a href="#objectcount" title="ObjectCount">ObjectCount</a>" : <i>Double</i>,
    "<a href="#profilecount" title="ProfileCount">ProfileCount</a>" : <i>Double</i>,
    "<a href="#totalsize" title="TotalSize">TotalSize</a>" : <i>Double</i>
}
</pre>

### YAML

<pre>
<a href="#meteringprofilecount" title="MeteringProfileCount">MeteringProfileCount</a>: <i>Double</i>
<a href="#objectcount" title="ObjectCount">ObjectCount</a>: <i>Double</i>
<a href="#profilecount" title="ProfileCount">ProfileCount</a>: <i>Double</i>
<a href="#totalsize" title="TotalSize">TotalSize</a>: <i>Double</i>
</pre>

## Properties

#### MeteringProfileCount

The number of profiles that you are currently paying for in the domain. If you have more than 100 objects associated with a single profile, that profile counts as two profiles. If you have more than 200 objects, that profile counts as three, and so on.

_Required_: No

_Type_: Double

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ObjectCount

The total number of objects in domain.

_Required_: No

_Type_: Double

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ProfileCount

The total number of profiles currently in the domain.

_Required_: No

_Type_: Double

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### TotalSize

The total size, in bytes, of all objects in the domain.

_Required_: No

_Type_: Double

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

