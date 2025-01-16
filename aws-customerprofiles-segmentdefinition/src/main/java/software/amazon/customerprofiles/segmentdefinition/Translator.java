package software.amazon.customerprofiles.segmentdefinition;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.customerprofiles.model.AddressDimension;
import software.amazon.awssdk.services.customerprofiles.model.ConditionOverrides;
import software.amazon.awssdk.services.customerprofiles.model.RangeOverride;
import software.amazon.awssdk.services.customerprofiles.model.AttributeDimension;
import software.amazon.awssdk.services.customerprofiles.model.CalculatedAttributeDimension;
import software.amazon.awssdk.services.customerprofiles.model.DateDimension;
import software.amazon.awssdk.services.customerprofiles.model.Dimension;
import software.amazon.awssdk.services.customerprofiles.model.ExtraLengthValueProfileDimension;
import software.amazon.awssdk.services.customerprofiles.model.Group;
import software.amazon.awssdk.services.customerprofiles.model.ProfileAttributes;
import software.amazon.awssdk.services.customerprofiles.model.ProfileDimension;
import software.amazon.awssdk.services.customerprofiles.model.SegmentGroup;
import software.amazon.awssdk.services.customerprofiles.model.SourceSegment;
import software.amazon.cloudformation.exceptions.BaseHandlerException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnUnauthorizedTaggingOperationException;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

/**
 * This class is a centralized placeholder for - api request construction - object translation
 * to/from aws sdk - resource model construction for read/list handlers
 */
public class Translator {

    private static final String ARN_FORMAT = "arn:%s:profile:%s:%s:domains/%s/segment-definitions/%s";
    private static final String NOT_AUTHORIZED_TO_PERFORM = "is not authorized to perform";
    private static final String TAG_RESOURCE_PERMISSION = "profile:TagResource";
    private static final String UNTAG_RESOURCE_PERMISSION = "profile:UntagResource";
    private static final String LIST_TAGS_FOR_RESOURCE_PERMISSION = "profile:ListTagsForResource";

    public static Set<Tag> mapTagsToSet(Map<String, String> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }

        return tags.entrySet().stream()
                .map(t -> Tag.builder().key(t.getKey()).value(t.getValue()).build())
                .collect(Collectors.toSet());
    }

    public static String toSegmentDefinitionArn(final ResourceHandlerRequest<ResourceModel> request) {
        return String.format(ARN_FORMAT, request.getAwsPartition(), request.getRegion(), request.getAwsAccountId(),
                request.getDesiredResourceState().getDomainName(), request.getDesiredResourceState().getSegmentDefinitionName());
    }

  public static SegmentGroup translateFromInternalSegmentGroup(
      software.amazon.customerprofiles.segmentdefinition.SegmentGroup source) {
    if (source == null) {
      return null;
    }

    return SegmentGroup.builder()
        .groups(source.getGroups() != null ? source.getGroups().stream()
            .map(group -> Group.builder()
                .dimensions(group.getDimensions() != null ? group.getDimensions().stream()
                    .map(dimension -> fromInternalSegmentGroupDimension(dimension))
                    .collect(Collectors.toList()) : new ArrayList<>())
                .sourceSegments(
                    group.getSourceSegments() != null ? group.getSourceSegments().stream()
                        .map(sourceSegment -> SourceSegment.builder()
                            .segmentDefinitionName(sourceSegment.getSegmentDefinitionName())
                            .build())
                        .collect(Collectors.toList()) : new ArrayList<>())
                .sourceType(group.getSourceType())
                .type(group.getType())
                .build())
            .collect(Collectors.toList()) : new ArrayList<Group>())
        .include(source.getInclude())
        .build();
  }

  public static software.amazon.customerprofiles.segmentdefinition.SegmentGroup translateToInternalSegmentGroup(
      SegmentGroup source) {
    if (source == null) {
      return null;
    }

    return software.amazon.customerprofiles.segmentdefinition.SegmentGroup.builder()
        .groups(source.groups() != null ? source.groups().stream()
            .map(group -> software.amazon.customerprofiles.segmentdefinition.Group.builder()
                .dimensions(group.dimensions() != null ? group.dimensions().stream()
                    .map(dimension -> toInternalSegmentGroupDimension(dimension))
                    .collect(Collectors.toList()) : new ArrayList<>())
                .sourceSegments(group.sourceSegments() != null ? group.sourceSegments().stream()
                    .map(
                        sourceSegment -> software.amazon.customerprofiles.segmentdefinition.SourceSegment.builder()
                            .segmentDefinitionName(sourceSegment.segmentDefinitionName())
                            .build())
                    .collect(Collectors.toList()) : new ArrayList<>())
                .sourceType(group.sourceType().toString())
                .type(group.type().toString())
                .build())
            .collect(Collectors.toList())
            : new ArrayList<software.amazon.customerprofiles.segmentdefinition.Group>())
        .include(source.include().toString())
        .build();
  }

  private static Dimension fromInternalSegmentGroupDimension(
      software.amazon.customerprofiles.segmentdefinition.Dimension source) {
    if (source == null) {
      return null;
    }

    Map<String, CalculatedAttributeDimension> calculatedAttributes = source.getCalculatedAttributes() == null ? null :
            source.getCalculatedAttributes().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> fromInternalCalculatedAttributeDimension(entry.getValue())));

    return Dimension.builder()
        .profileAttributes(fromInternalProfileAttributes(source.getProfileAttributes()))
        .calculatedAttributes(calculatedAttributes)
        .build();
  }

  private static software.amazon.customerprofiles.segmentdefinition.Dimension toInternalSegmentGroupDimension(
      software.amazon.awssdk.services.customerprofiles.model.Dimension source) {
    if (source == null) {
      return null;
    }

    Map<String, software.amazon.customerprofiles.segmentdefinition.CalculatedAttributeDimension> calculatedAttributes =
            source.calculatedAttributes() == null ? null : source.calculatedAttributes().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> toInternalCalculatedAttributeDimension(entry.getValue())));

    return software.amazon.customerprofiles.segmentdefinition.Dimension.builder()
        .profileAttributes(toInternalProfileAttributes(source.profileAttributes()))
        .calculatedAttributes(calculatedAttributes)
        .build();
  }

  private static software.amazon.customerprofiles.segmentdefinition.ProfileAttributes toInternalProfileAttributes(
      ProfileAttributes source) {
    if (source == null) {
      return null;
    }

    return software.amazon.customerprofiles.segmentdefinition.ProfileAttributes.builder()
        .accountNumber(toInternalProfileDimension(source.accountNumber()))
        .additionalInformation(
            toInternalExtraLengthValueProfileDimension(source.additionalInformation()))
        .firstName(toInternalProfileDimension(source.firstName()))
        .lastName(toInternalProfileDimension(source.lastName()))
        .middleName(toInternalProfileDimension(source.middleName()))
        .genderString(toInternalProfileDimension(source.genderString()))
        .partyTypeString(toInternalProfileDimension(source.partyTypeString()))
        .birthDate(toInternalDateDimension(source.birthDate()))
        .phoneNumber(toInternalProfileDimension(source.phoneNumber()))
        .businessName(toInternalProfileDimension(source.businessName()))
        .businessPhoneNumber(toInternalProfileDimension(source.businessPhoneNumber()))
        .homePhoneNumber(toInternalProfileDimension(source.homePhoneNumber()))
        .mobilePhoneNumber(toInternalProfileDimension(source.mobilePhoneNumber()))
        .emailAddress(toInternalProfileDimension(source.emailAddress()))
        .personalEmailAddress(toInternalProfileDimension(source.personalEmailAddress()))
        .businessEmailAddress(toInternalProfileDimension(source.businessEmailAddress()))
        .address(toInternalAddressDimension(source.address()))
        .shippingAddress(toInternalAddressDimension(source.shippingAddress()))
        .mailingAddress(toInternalAddressDimension(source.mailingAddress()))
        .billingAddress(toInternalAddressDimension(source.billingAddress()))
        .attributes(source.attributes() != null ? source.attributes().entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> toInternalAttributeDimension(entry.getValue())
            )) : null)
        .build();
  }

  private static ProfileAttributes fromInternalProfileAttributes(
      software.amazon.customerprofiles.segmentdefinition.ProfileAttributes source) {
    if (source == null) {
      return null;
    }

    return ProfileAttributes.builder()
        .accountNumber(fromInternalProfileDimension(source.getAccountNumber()))
        .additionalInformation(
            fromInternalExtraLengthValueProfileDimension(source.getAdditionalInformation()))
        .firstName(fromInternalProfileDimension(source.getFirstName()))
        .lastName(fromInternalProfileDimension(source.getLastName()))
        .middleName(fromInternalProfileDimension(source.getMiddleName()))
        .genderString(fromInternalProfileDimension(source.getGenderString()))
        .partyTypeString(fromInternalProfileDimension(source.getPartyTypeString()))
        .birthDate(fromInternalDateDimension(source.getBirthDate()))
        .phoneNumber(fromInternalProfileDimension(source.getPhoneNumber()))
        .businessName(fromInternalProfileDimension(source.getBusinessName()))
        .businessPhoneNumber(fromInternalProfileDimension(source.getBusinessPhoneNumber()))
        .homePhoneNumber(fromInternalProfileDimension(source.getHomePhoneNumber()))
        .mobilePhoneNumber(fromInternalProfileDimension(source.getMobilePhoneNumber()))
        .emailAddress(fromInternalProfileDimension(source.getEmailAddress()))
        .personalEmailAddress(fromInternalProfileDimension(source.getPersonalEmailAddress()))
        .businessEmailAddress(fromInternalProfileDimension(source.getBusinessEmailAddress()))
        .address(fromInternalAddressDimension(source.getAddress()))
        .shippingAddress(fromInternalAddressDimension(source.getShippingAddress()))
        .mailingAddress(fromInternalAddressDimension(source.getMailingAddress()))
        .billingAddress(fromInternalAddressDimension(source.getBillingAddress()))
        .attributes(source.getAttributes() != null ? source.getAttributes().entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> fromInternalAttributeDimension(entry.getValue())
            )) : null)
        .build();
  }

  private static AttributeDimension fromInternalAttributeDimension(
      software.amazon.customerprofiles.segmentdefinition.AttributeDimension source) {
    if (source == null) {
      return null;
    }

    return AttributeDimension.builder()
        .dimensionType(source.getDimensionType())
        .values(source.getValues())
        .build();
  }

  private static software.amazon.customerprofiles.segmentdefinition.AttributeDimension toInternalAttributeDimension(
      AttributeDimension source) {
    if (source == null) {
      return null;
    }

    return software.amazon.customerprofiles.segmentdefinition.AttributeDimension.builder()
        .dimensionType(source.dimensionType().toString())
        .values(source.values())
        .build();
  }

  private static CalculatedAttributeDimension fromInternalCalculatedAttributeDimension(
          software.amazon.customerprofiles.segmentdefinition.CalculatedAttributeDimension source) {
      if (source == null) {
        return null;
      }

      return CalculatedAttributeDimension.builder()
              .dimensionType(source.getDimensionType())
              .values(source.getValues())
              .conditionOverrides(fromInternalConditionOverrides(source.getConditionOverrides()))
              .build();
  }

  private static software.amazon.customerprofiles.segmentdefinition.CalculatedAttributeDimension toInternalCalculatedAttributeDimension(
          CalculatedAttributeDimension source) {
      if (source == null) {
        return null;
      }

      return software.amazon.customerprofiles.segmentdefinition.CalculatedAttributeDimension.builder()
              .dimensionType(source.dimensionType().toString())
              .values(source.values())
              .conditionOverrides(toInternalConditionOverrides(source.conditionOverrides()))
              .build();
  }

    private static ConditionOverrides fromInternalConditionOverrides(
            software.amazon.customerprofiles.segmentdefinition.ConditionOverrides source) {
        if (source == null) {
            return null;
        }

        return ConditionOverrides.builder()
                .range(source.getRange() == null ? null : RangeOverride.builder()
                        .start(source.getRange().getStart())
                        .end(source.getRange().getEnd())
                        .unit(source.getRange().getUnit())
                        .build())
                .build();
    }

    private static software.amazon.customerprofiles.segmentdefinition.ConditionOverrides toInternalConditionOverrides(
            ConditionOverrides source) {
        if (source == null) {
            return null;
        }

        return software.amazon.customerprofiles.segmentdefinition.ConditionOverrides.builder()
                .range(source.range() == null ? null : software.amazon.customerprofiles.segmentdefinition.RangeOverride.builder()
                        .start(source.range().start())
                        .end(source.range().end())
                        .unit(source.range().unit().toString())
                        .build())
                .build();
    }

  private static ProfileDimension fromInternalProfileDimension(
      software.amazon.customerprofiles.segmentdefinition.ProfileDimension source) {
    if (source == null) {
      return null;
    }

    return ProfileDimension.builder()
        .dimensionType(source.getDimensionType())
        .values(source.getValues())
        .build();
  }

  private static software.amazon.customerprofiles.segmentdefinition.ProfileDimension toInternalProfileDimension(
      ProfileDimension source) {
    if (source == null) {
      return null;
    }

    return software.amazon.customerprofiles.segmentdefinition.ProfileDimension.builder()
        .dimensionType(source.dimensionType().toString())
        .values(source.values())
        .build();
  }

  private static ExtraLengthValueProfileDimension fromInternalExtraLengthValueProfileDimension(
      software.amazon.customerprofiles.segmentdefinition.ExtraLengthValueProfileDimension source) {
    if (source == null) {
      return null;
    }

    return ExtraLengthValueProfileDimension.builder()
        .dimensionType(source.getDimensionType())
        .values(source.getValues())
        .build();
  }

  private static software.amazon.customerprofiles.segmentdefinition.ExtraLengthValueProfileDimension toInternalExtraLengthValueProfileDimension(
      ExtraLengthValueProfileDimension source) {
    if (source == null) {
      return null;
    }

    return software.amazon.customerprofiles.segmentdefinition.ExtraLengthValueProfileDimension.builder()
        .dimensionType(source.dimensionType().toString())
        .values(source.values())
        .build();
  }

  private static DateDimension fromInternalDateDimension(
      software.amazon.customerprofiles.segmentdefinition.DateDimension source) {
    if (source == null) {
      return null;
    }

    return DateDimension.builder()
        .dimensionType(source.getDimensionType())
        .values(source.getValues())
        .build();
  }

  private static software.amazon.customerprofiles.segmentdefinition.DateDimension toInternalDateDimension(
      DateDimension source) {
    if (source == null) {
      return null;
    }

    return software.amazon.customerprofiles.segmentdefinition.DateDimension.builder()
        .dimensionType(source.dimensionType().toString())
        .values(source.values())
        .build();
  }

  private static AddressDimension fromInternalAddressDimension(
      software.amazon.customerprofiles.segmentdefinition.AddressDimension source) {
    if (source == null) {
      return null;
    }

    return AddressDimension.builder()
        .city(fromInternalProfileDimension(source.getCity()))
        .country(fromInternalProfileDimension(source.getCountry()))
        .county(fromInternalProfileDimension(source.getCounty()))
        .postalCode(fromInternalProfileDimension(source.getPostalCode()))
        .province(fromInternalProfileDimension(source.getProvince()))
        .state(fromInternalProfileDimension(source.getState()))
        .build();
  }

  private static software.amazon.customerprofiles.segmentdefinition.AddressDimension toInternalAddressDimension(
      AddressDimension source) {
    if (source == null) {
      return null;
    }

    return software.amazon.customerprofiles.segmentdefinition.AddressDimension.builder()
        .city(toInternalProfileDimension(source.city()))
        .country(toInternalProfileDimension(source.country()))
        .county(toInternalProfileDimension(source.county()))
        .postalCode(toInternalProfileDimension(source.postalCode()))
        .province(toInternalProfileDimension(source.province()))
        .state(toInternalProfileDimension(source.state()))
        .build();
  }

    public static BaseHandlerException translateToCfnException(Exception e) {
        if (isTagSupportDenied(e)) {
            return new CfnUnauthorizedTaggingOperationException(e);
        } else {
            return new CfnGeneralServiceException(e);
        }
    }

    private static boolean isTagSupportDenied(Exception e) {
        return (e.getMessage() != null &&
                e.getMessage().contains(NOT_AUTHORIZED_TO_PERFORM) &&
                e.getMessage().contains(TAG_RESOURCE_PERMISSION) ||
                e.getMessage().contains(UNTAG_RESOURCE_PERMISSION) ||
                e.getMessage().contains(LIST_TAGS_FOR_RESOURCE_PERMISSION));
    }
}
