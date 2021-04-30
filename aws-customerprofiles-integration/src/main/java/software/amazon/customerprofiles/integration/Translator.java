package software.amazon.customerprofiles.integration;

import com.amazonaws.util.StringUtils;
import com.google.common.collect.ImmutableMap;
import software.amazon.awssdk.services.customerprofiles.model.FlowDefinition;
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
}

