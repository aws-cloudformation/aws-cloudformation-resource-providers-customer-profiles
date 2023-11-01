# AWS::CustomerProfiles::Domain ExportingConfig

Configuration information for exporting Identity Resolution results, for example, to an S3 bucket.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#s3exporting" title="S3Exporting">S3Exporting</a>" : <i><a href="s3exportingconfig.md">S3ExportingConfig</a></i>
}
</pre>

### YAML

<pre>
<a href="#s3exporting" title="S3Exporting">S3Exporting</a>: <i><a href="s3exportingconfig.md">S3ExportingConfig</a></i>
</pre>

## Properties

#### S3Exporting

The S3 location where Identity Resolution Jobs write result files.

_Required_: No

_Type_: <a href="s3exportingconfig.md">S3ExportingConfig</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

