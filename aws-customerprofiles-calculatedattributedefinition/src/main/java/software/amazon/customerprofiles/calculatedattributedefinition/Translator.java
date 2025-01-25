package software.amazon.customerprofiles.calculatedattributedefinition;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.customerprofiles.model.AttributeDetails;
import software.amazon.awssdk.services.customerprofiles.model.AttributeItem;
import software.amazon.awssdk.services.customerprofiles.model.Conditions;
import software.amazon.awssdk.services.customerprofiles.model.Range;
import software.amazon.awssdk.services.customerprofiles.model.Threshold;
import software.amazon.cloudformation.exceptions.BaseHandlerException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnUnauthorizedTaggingOperationException;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class Translator {
    public static final String ARN_FORMAT = "arn:%s:profile:%s:%s:domains/%s/calculated-attributes/%s";
    private static final String NOT_AUTHORIZED_TO_PERFORM = "is not authorized to perform";
    private static final String TAG_RESOURCE_PERMISSION = "profile:TagResource";
    private static final String UNTAG_RESOURCE_PERMISSION = "profile:UntagResource";
    private static final String LIST_TAGS_FOR_RESOURCE_PERMISSION = "profile:ListTagsForResource";

    public static String toCalculatedAttributeDefinitionArn(final ResourceHandlerRequest<ResourceModel> request) {
        return String.format(ARN_FORMAT, request.getAwsPartition(), request.getRegion(), request.getAwsAccountId(),
                request.getDesiredResourceState().getDomainName(), request.getDesiredResourceState().getCalculatedAttributeName());
    }

    public static AttributeDetails translateFromInternalAttributeDetails(
            software.amazon.customerprofiles.calculatedattributedefinition.AttributeDetails source) {
        if (source == null) {
            return null;
        }

        return AttributeDetails.builder()
                .attributes(source.getAttributes().stream().map(attributeItem -> AttributeItem.builder()
                        .name(attributeItem.getName()).build()).collect(Collectors.toList()))
                .expression(source.getExpression())
                .build();
    }

    public static software.amazon.customerprofiles.calculatedattributedefinition.AttributeDetails translateToInternalAttributeDetails(
            AttributeDetails source) {
        if (source == null) {
            return null;
        }

        return software.amazon.customerprofiles.calculatedattributedefinition.AttributeDetails.builder()
                .attributes(source.attributes().stream().map(attributeItem ->
                        software.amazon.customerprofiles.calculatedattributedefinition.AttributeItem.builder()
                                .name(attributeItem.name()).build()).collect(Collectors.toSet()))
                .expression(source.expression())
                .build();
    }

    public static Conditions translateFromInternalConditions(
            software.amazon.customerprofiles.calculatedattributedefinition.Conditions source) {
        if (source == null) {
            return null;
        }

        return Conditions.builder()
                .range(source.getRange() == null ? null : Range.builder()
                        .value(source.getRange().getValue())
                        .unit(source.getRange().getUnit())
                        .build())
                .objectCount(source.getObjectCount())
                .threshold(source.getThreshold() == null ? null : Threshold.builder()
                        .value(source.getThreshold().getValue())
                        .operator(source.getThreshold().getOperator())
                        .build())
                .build();
    }

    public static software.amazon.customerprofiles.calculatedattributedefinition.Conditions translateToInternalConditions(Conditions source) {
        if (source == null) {
            return null;
        }

        return software.amazon.customerprofiles.calculatedattributedefinition.Conditions.builder()
                .range(source.range() == null ? null : software.amazon.customerprofiles.calculatedattributedefinition.Range.builder()
                        .value(source.range().value())
                        .unit(source.range().unitAsString())
                        .build())
                .objectCount(source.objectCount())
                .threshold(source.threshold() == null ? null : software.amazon.customerprofiles.calculatedattributedefinition.Threshold.builder()
                        .value(source.threshold().value())
                        .operator(source.threshold().operatorAsString())
                        .build())
                .build();
    }

    public static Set<Tag> mapTagsToSet(Map<String, String> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }

        return tags.entrySet().stream().map(t -> Tag.builder().key(t.getKey()).value(t.getValue()).build())
                .collect(Collectors.toSet());
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
