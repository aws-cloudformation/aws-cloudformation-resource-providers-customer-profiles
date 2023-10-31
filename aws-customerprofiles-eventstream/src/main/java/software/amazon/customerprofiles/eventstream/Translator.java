package software.amazon.customerprofiles.eventstream;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import software.amazon.awssdk.services.customerprofiles.model.EventStreamDestinationDetails;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class Translator {
    static String ARN_FORMAT = "arn:%s:profile:%s:%s:domains/%s/event-streams/%s";

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
}