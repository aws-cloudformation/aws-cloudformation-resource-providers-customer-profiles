# AWS::CustomerProfiles::Integration TaskPropertiesMap

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#operatorpropertykey" title="OperatorPropertyKey">OperatorPropertyKey</a>" : <i>String</i>,
    "<a href="#property" title="Property">Property</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#operatorpropertykey" title="OperatorPropertyKey">OperatorPropertyKey</a>: <i>String</i>
<a href="#property" title="Property">Property</a>: <i>String</i>
</pre>

## Properties

#### OperatorPropertyKey

_Required_: Yes

_Type_: String

_Allowed Values_: <code>VALUE</code> | <code>VALUES</code> | <code>DATA_TYPE</code> | <code>UPPER_BOUND</code> | <code>LOWER_BOUND</code> | <code>SOURCE_DATA_TYPE</code> | <code>DESTINATION_DATA_TYPE</code> | <code>VALIDATION_ACTION</code> | <code>MASK_VALUE</code> | <code>MASK_LENGTH</code> | <code>TRUNCATE_LENGTH</code> | <code>MATH_OPERATION_FIELDS_ORDER</code> | <code>CONCAT_FORMAT</code> | <code>SUBFIELD_CATEGORY_MAP</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Property

_Required_: Yes

_Type_: String

_Maximum Length_: <code>2048</code>

_Pattern_: <code>.+</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

