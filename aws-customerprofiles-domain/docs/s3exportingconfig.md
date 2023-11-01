# AWS::CustomerProfiles::Domain S3ExportingConfig

The S3 location where Identity Resolution Jobs write result files.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#s3bucketname" title="S3BucketName">S3BucketName</a>" : <i>String</i>,
    "<a href="#s3keyname" title="S3KeyName">S3KeyName</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#s3bucketname" title="S3BucketName">S3BucketName</a>: <i>String</i>
<a href="#s3keyname" title="S3KeyName">S3KeyName</a>: <i>String</i>
</pre>

## Properties

#### S3BucketName

The name of the S3 bucket where Identity Resolution Jobs write result files.

_Required_: Yes

_Type_: String

_Minimum Length_: <code>3</code>

_Maximum Length_: <code>63</code>

_Pattern_: <code>^[a-z0-9.-]+$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### S3KeyName

The S3 key name of the location where Identity Resolution Jobs write result files.

_Required_: No

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>800</code>

_Pattern_: <code>.*</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
