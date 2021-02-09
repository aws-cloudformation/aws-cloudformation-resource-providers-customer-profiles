package software.amazon.customerprofiles.integration;

import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Translator {

    static String ARN_FORMAT = "arn:%s:profile:%s:%s:domains/%s/integrations/%s";

    static String toIntegrationArn(final ResourceHandlerRequest<ResourceModel> request) {
        return String.format(ARN_FORMAT, request.getAwsPartition(), request.getRegion(), request.getAwsAccountId(),
                request.getDesiredResourceState().getDomainName(), request.getDesiredResourceState().getUri());
    }

    static List<Tag> mapTagsToList(Map<String, String> tags) {
        if (tags == null || tags.isEmpty()) {
            return null;
        }
        return tags.entrySet().stream()
                .map(t -> Tag.builder()
                        .key(t.getKey())
                        .value(t.getValue()).build())
                .collect(Collectors.toList());
    }

}
