package software.amazon.customerprofiles.eventstream;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import software.amazon.awssdk.services.customerprofiles.model.EventStreamDestinationDetails;
import software.amazon.cloudformation.exceptions.BaseHandlerException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnUnauthorizedTaggingOperationException;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class Translator {
    static String ARN_FORMAT = "arn:%s:profile:%s:%s:domains/%s/event-streams/%s";
    private static final String NOT_AUTHORIZED_TO_PERFORM = "is not authorized to perform";
    private static final String TAG_RESOURCE_PERMISSION = "profile:TagResource";
    private static final String UNTAG_RESOURCE_PERMISSION = "profile:UntagResource";
    private static final String LIST_TAGS_FOR_RESOURCE_PERMISSION = "profile:ListTagsForResource";

    static String toEventStreamArn(final ResourceHandlerRequest<ResourceModel> request) {
        return String.format(ARN_FORMAT, request.getAwsPartition(), request.getRegion(), request.getAwsAccountId(),
            request.getDesiredResourceState().getDomainName(), request.getDesiredResourceState().getEventStreamName());
    }

    public static Set<Tag> mapTagsToSet(Map<String, String> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }

        return tags.entrySet().stream().map(t -> Tag.builder().key(t.getKey()).value(t.getValue()).build())
            .collect(Collectors.toSet());
    }

    public static DestinationDetails translateToInternalDestinationDetails(EventStreamDestinationDetails source) {
        if (source == null) {
            return null;
        }

        DestinationDetails.DestinationDetailsBuilder destinationDetailsBuilder =
            software.amazon.customerprofiles.eventstream.DestinationDetails.builder()
                .uri(source.uri())
                .status(source.status().toString());

        return destinationDetailsBuilder.build();
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
