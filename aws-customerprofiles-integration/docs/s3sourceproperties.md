# AWS::CustomerProfiles::Integration S3SourceProperties

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#bucketname" title="BucketName">BucketName</a>" : <i>String</i>,
    "<a href="#bucketprefix" title="BucketPrefix">BucketPrefix</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#bucketname" title="BucketName">BucketName</a>: <i>String</i>
<a href="#bucketprefix" title="BucketPrefix">BucketPrefix</a>: <i>String</i>
</pre>

## Properties

#### BucketName

_Required_: Yes

_Type_: String

_Minimum Length_: <code>3</code>

_Maximum Length_: <code>63</code>

_Pattern_: <code>\S+</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### BucketPrefix

_Required_: No

_Type_: String

_Maximum Length_: <code>512</code>

_Pattern_: <code>.*</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
