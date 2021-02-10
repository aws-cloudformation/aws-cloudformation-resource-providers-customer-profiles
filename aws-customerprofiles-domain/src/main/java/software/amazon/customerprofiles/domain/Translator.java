package software.amazon.customerprofiles.domain;

import com.google.common.collect.Lists;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Translator {

    static String ARN_FORMAT = "arn:%s:profile:%s:%s:domains/%s";

    static String toDomainARN(final ResourceHandlerRequest<ResourceModel> request) {
        return String.format(ARN_FORMAT, request.getAwsPartition(), request.getRegion(), request.getAwsAccountId(),
                request.getDesiredResourceState().getDomainName());
    }

    static List<Tag> mapTagsToList(Map<String, String> tags) {
        if (tags.isEmpty()) {
            return null;
        }
        return tags.entrySet().stream()
                .map(t -> Tag.builder()
                        .key(t.getKey())
                        .value(t.getValue()).build())
                .collect(Collectors.toList());
    }

}
