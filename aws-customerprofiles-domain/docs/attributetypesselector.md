# AWS::CustomerProfiles::Domain AttributeTypesSelector

Configures information about the AttributeTypesSelector where the rule-based identity resolution uses to match profiles.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#attributematchingmodel" title="AttributeMatchingModel">AttributeMatchingModel</a>" : <i>String</i>,
    "<a href="#address" title="Address">Address</a>" : <i>[ String, ... ]</i>,
    "<a href="#emailaddress" title="EmailAddress">EmailAddress</a>" : <i>[ String, ... ]</i>,
    "<a href="#phonenumber" title="PhoneNumber">PhoneNumber</a>" : <i>[ String, ... ]</i>
}
</pre>

### YAML

<pre>
<a href="#attributematchingmodel" title="AttributeMatchingModel">AttributeMatchingModel</a>: <i>String</i>
<a href="#address" title="Address">Address</a>: <i>
      - String</i>
<a href="#emailaddress" title="EmailAddress">EmailAddress</a>: <i>
      - String</i>
<a href="#phonenumber" title="PhoneNumber">PhoneNumber</a>: <i>
      - String</i>
</pre>

## Properties

#### AttributeMatchingModel

Configures the AttributeMatchingModel, you can either choose ONE_TO_ONE or MANY_TO_MANY.

_Required_: Yes

_Type_: String

_Allowed Values_: <code>ONE_TO_ONE</code> | <code>MANY_TO_MANY</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Address

The Address type. You can choose from Address, BusinessAddress, MaillingAddress, and ShippingAddress. You only can use the Address type in the MatchingRule. For example, if you want to match profile based on BusinessAddress.City or MaillingAddress.City, you need to choose the BusinessAddress and the MaillingAddress to represent the Address type and specify the Address.City on the matching rule.

_Required_: No

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### EmailAddress

The Email type. You can choose from EmailAddress, BusinessEmailAddress and PersonalEmailAddress. You only can use the EmailAddress type in the MatchingRule. For example, if you want to match profile based on PersonalEmailAddress or BusinessEmailAddress, you need to choose the PersonalEmailAddress and the BusinessEmailAddress to represent the EmailAddress type and only specify the EmailAddress on the matching rule.

_Required_: No

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### PhoneNumber

The PhoneNumber type. You can choose from PhoneNumber, HomePhoneNumber, and MobilePhoneNumber. You only can use the PhoneNumber type in the MatchingRule. For example, if you want to match a profile based on Phone or HomePhone, you need to choose the Phone and the HomePhone to represent the PhoneNumber type and only specify the PhoneNumber on the matching rule.

_Required_: No

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
