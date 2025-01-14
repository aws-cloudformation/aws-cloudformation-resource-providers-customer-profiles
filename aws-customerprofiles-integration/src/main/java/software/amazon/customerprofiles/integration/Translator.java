package software.amazon.customerprofiles.integration;

import com.amazonaws.util.StringUtils;
import com.google.common.collect.ImmutableMap;
import software.amazon.awssdk.services.customerprofiles.model.FlowDefinition;
import software.amazon.cloudformation.exceptions.BaseHandlerException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnUnauthorizedTaggingOperationException;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import software.amazon.customerprofiles.integration.translators.ConnectorTranslator;
import software.amazon.customerprofiles.integration.translators.MarketoTranslator;
import software.amazon.customerprofiles.integration.translators.S3Translator;
import software.amazon.customerprofiles.integration.translators.SalesforceTranslator;
import software.amazon.customerprofiles.integration.translators.ServiceNowTranslator;
import software.amazon.customerprofiles.integration.translators.ZendeskTranslator;
import static software.amazon.customerprofiles.integration.translators.TaskTranslator.toServiceTasks;
import static software.amazon.customerprofiles.integration.translators.TriggerConfigTranslator.toServiceTriggerConfig;

public class Translator {

    static String ARN_FORMAT = "arn:%s:profile:%s:%s:domains/%s/integrations/%s";
    private static final String NOT_AUTHORIZED_TO_PERFORM = "is not authorized to perform";
    private static final String TAG_RESOURCE_PERMISSION = "profile:TagResource";
    private static final String UNTAG_RESOURCE_PERMISSION = "profile:UntagResource";
    private static final String LIST_TAGS_FOR_RESOURCE_PERMISSION = "profile:ListTagsForResource";

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

    static Map<String, String> mapListToObjectTypeNames(List<ObjectTypeMapping> list) {
        if (list == null || list.size() == 0) {
            return null;
        }
        return list.stream().collect(Collectors.toMap(ObjectTypeMapping::getKey, ObjectTypeMapping::getValue));
    }

    static List<ObjectTypeMapping> mapObjectTypeNamesToList(Map<String, String> objectTypeNames) {
        if (objectTypeNames == null || objectTypeNames.isEmpty()) {
            return null;
        }
        return objectTypeNames.entrySet().stream()
                .map(t -> ObjectTypeMapping.builder()
                        .key(t.getKey())
                        .value(t.getValue()).build())
                .collect(Collectors.toList());
    }

    private static Map<String, ConnectorTranslator> connectorTranslators = ImmutableMap.<String, ConnectorTranslator>builder()
            .put("Salesforce", new SalesforceTranslator())
            .put("Marketo", new MarketoTranslator())
            .put("ServiceNow", new ServiceNowTranslator())
            .put("S3", new S3Translator())
            .put("Zendesk", new ZendeskTranslator())
            .build();

    public static ConnectorTranslator getTranslator(String connectorType) {
        return connectorTranslators.get(connectorType);
    }

    public static FlowDefinition buildServiceFlowDefinition(software.amazon.customerprofiles.integration.FlowDefinition model) {
        if (model == null) {
            return null;
        }

        String connectorType = model.getSourceFlowConfig().getConnectorType();
        ConnectorTranslator connector = getTranslator(connectorType);
        return FlowDefinition.builder()
                .description(!StringUtils.isNullOrEmpty(model.getDescription()) ? model.getDescription() : null)
                .flowName(model.getFlowName())
                .kmsArn(model.getKmsArn())
                .sourceFlowConfig(connector.toServiceSourceFlowConfig(model.getSourceFlowConfig()))
                .tasks(toServiceTasks(model.getTasks(), connectorType))
                .triggerConfig(toServiceTriggerConfig(model.getTriggerConfig()))
                .build();
    }

    public static List<String> getValidEventTriggerNames(List<String> eventTriggerNames) {
        if (eventTriggerNames == null || eventTriggerNames.isEmpty()) {
            return null;
        }

        return eventTriggerNames;
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
