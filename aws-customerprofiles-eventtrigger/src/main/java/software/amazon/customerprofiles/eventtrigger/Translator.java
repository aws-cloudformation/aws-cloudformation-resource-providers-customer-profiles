package software.amazon.customerprofiles.eventtrigger;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import software.amazon.awssdk.services.customerprofiles.model.EventTriggerCondition;
import software.amazon.awssdk.services.customerprofiles.model.EventTriggerDimension;
import software.amazon.awssdk.services.customerprofiles.model.EventTriggerLimits;
import software.amazon.awssdk.services.customerprofiles.model.ObjectAttribute;
import software.amazon.awssdk.services.customerprofiles.model.Period;
import software.amazon.cloudformation.exceptions.BaseHandlerException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnUnauthorizedTaggingOperationException;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class Translator {
    public static final String ARN_FORMAT = "arn:%s:profile:%s:%s:domains/%s/event-triggers/%s";
    private static final String NOT_AUTHORIZED_TO_PERFORM = "is not authorized to perform";
    private static final String TAG_RESOURCE_PERMISSION = "profile:TagResource";
    private static final String UNTAG_RESOURCE_PERMISSION = "profile:UntagResource";
    private static final String LIST_TAGS_FOR_RESOURCE_PERMISSION = "profile:ListTagsForResource";

    public static String toEventTriggerArn(final ResourceHandlerRequest<ResourceModel> request) {
        return String.format(ARN_FORMAT, request.getAwsPartition(), request.getRegion(), request.getAwsAccountId(),
                request.getDesiredResourceState().getDomainName(), request.getDesiredResourceState().getEventTriggerName());
    }

    public static List<EventTriggerCondition> translateFromInternalEventTriggerConditions(
            List<software.amazon.customerprofiles.eventtrigger.EventTriggerCondition> source) {
        if (source == null) {
            return null;
        }

        return source.stream().map(Translator::translateFromInternalEventTriggerCondition).toList();
    }

    public static List<software.amazon.customerprofiles.eventtrigger.EventTriggerCondition> translateToInternalEventTriggerConditions(List<EventTriggerCondition> source) {
        if (source == null) {
            return null;
        }

        return source.stream().map(Translator::translateToInternalEventTriggerCondition).toList();
    }

    public static EventTriggerCondition translateFromInternalEventTriggerCondition(
            software.amazon.customerprofiles.eventtrigger.EventTriggerCondition source) {
        if (source == null) {
            return null;
        }

        List<EventTriggerDimension> eventTriggerDimensions = source.getEventTriggerDimensions().stream()
                .map(Translator::translateFromInternalEventTriggerDimension).toList();

        return EventTriggerCondition.builder()
                .eventTriggerDimensions(eventTriggerDimensions)
                .logicalOperator(source.getLogicalOperator())
                .build();
    }

    public static software.amazon.customerprofiles.eventtrigger.EventTriggerCondition translateToInternalEventTriggerCondition(EventTriggerCondition source) {
        if (source == null) {
            return null;
        }

        List<software.amazon.customerprofiles.eventtrigger.EventTriggerDimension> eventTriggerDimensions = source.eventTriggerDimensions().stream()
                .map(Translator::translateToInternalEventTriggerDimension).toList();

        return software.amazon.customerprofiles.eventtrigger.EventTriggerCondition.builder()
                .eventTriggerDimensions(eventTriggerDimensions)
                .logicalOperator(source.logicalOperator().name())
                .build();
    }

    public static EventTriggerDimension translateFromInternalEventTriggerDimension(
            software.amazon.customerprofiles.eventtrigger.EventTriggerDimension source) {
        if (source == null) {
            return null;
        }

        List<ObjectAttribute> objectAttributes = source.getObjectAttributes().stream()
                .map(Translator::translateFromInternalObjectAttribute).toList();

        return EventTriggerDimension.builder()
                .objectAttributes(objectAttributes)
                .build();
    }

    public static software.amazon.customerprofiles.eventtrigger.EventTriggerDimension translateToInternalEventTriggerDimension(EventTriggerDimension source) {
        if (source == null) {
            return null;
        }

        List<software.amazon.customerprofiles.eventtrigger.ObjectAttribute> objectAttributes = source.objectAttributes().stream()
                .map(Translator::translateToInternalObjectAttribute).toList();

        return software.amazon.customerprofiles.eventtrigger.EventTriggerDimension.builder()
                .objectAttributes(objectAttributes)
                .build();
    }

    public static ObjectAttribute translateFromInternalObjectAttribute(software.amazon.customerprofiles.eventtrigger.ObjectAttribute source) {
        if (source == null) {
            return null;
        }

        return ObjectAttribute.builder()
                .source(source.getSource())
                .fieldName(source.getFieldName())
                .comparisonOperator(source.getComparisonOperator())
                .values(source.getValues())
                .build();
    }

    public static software.amazon.customerprofiles.eventtrigger.ObjectAttribute translateToInternalObjectAttribute(ObjectAttribute source) {
        if (source == null) {
            return null;
        }

        return software.amazon.customerprofiles.eventtrigger.ObjectAttribute.builder()
                .source(source.source())
                .fieldName(source.fieldName())
                .comparisonOperator(source.comparisonOperator().name())
                .values(source.values())
                .build();
    }

    public static EventTriggerLimits translateFromInternalEventTriggerLimits(software.amazon.customerprofiles.eventtrigger.EventTriggerLimits source) {
        if (source == null) {
            return null;
        }

        List<Period> periods = source.getPeriods().stream().map(Translator::translateFromInternalPeriod).toList();

        return EventTriggerLimits.builder()
                .eventExpiration(source.getEventExpiration())
                .periods(periods)
                .build();
    }

    public static software.amazon.customerprofiles.eventtrigger.EventTriggerLimits translateToInternalEventTriggerLimits(EventTriggerLimits source) {
        if (source == null) {
            return null;
        }

        List<software.amazon.customerprofiles.eventtrigger.Period> periods = source.periods().stream().map(Translator::translateToInternalPeriod).toList();

        return software.amazon.customerprofiles.eventtrigger.EventTriggerLimits.builder()
                .eventExpiration(source.eventExpiration())
                .periods(periods)
                .build();
    }

    public static Period translateFromInternalPeriod(software.amazon.customerprofiles.eventtrigger.Period source) {
        if (source == null) {
            return null;
        }

        return Period.builder()
                .unlimited(source.getUnlimited())
                .maxInvocationsPerProfile(source.getMaxInvocationsPerProfile())
                .unit(source.getUnit())
                .value(source.getValue())
                .build();
    }

    public static software.amazon.customerprofiles.eventtrigger.Period translateToInternalPeriod(Period source) {
        if (source == null) {
            return null;
        }

        return software.amazon.customerprofiles.eventtrigger.Period.builder()
                .unlimited(source.unlimited())
                .maxInvocationsPerProfile(source.maxInvocationsPerProfile())
                .unit(source.unit().name())
                .value(source.value())
                .build();
    }

    public static Set<Tag> mapTagsToSet(Map<String, String> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }

        return tags.entrySet().stream().map(t -> Tag.builder().key(t.getKey()).value(t.getValue()).build())
                .collect(Collectors.toSet());
    }

    public static BaseHandlerException translateToCfnException(Exception exc) {
        if (isTagSupportDenied(exc)) {
            return new CfnUnauthorizedTaggingOperationException(exc);
        } else {
            return new CfnGeneralServiceException(exc);
        }
    }

    private static boolean isTagSupportDenied(Exception exc) {
        return (exc.getMessage() != null && exc.getMessage().contains(NOT_AUTHORIZED_TO_PERFORM) &&
                (exc.getMessage().contains(TAG_RESOURCE_PERMISSION) || exc.getMessage().contains(UNTAG_RESOURCE_PERMISSION) || exc.getMessage().contains(LIST_TAGS_FOR_RESOURCE_PERMISSION)));
    }
}
