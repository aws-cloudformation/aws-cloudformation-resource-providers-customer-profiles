package software.amazon.customerprofiles.integration.translators;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import software.amazon.customerprofiles.integration.ConnectorOperator;
import software.amazon.customerprofiles.integration.FlowDefinition;
import software.amazon.customerprofiles.integration.IncrementalPullConfig;
import software.amazon.customerprofiles.integration.MarketoSourceProperties;
import software.amazon.customerprofiles.integration.S3SourceProperties;
import software.amazon.customerprofiles.integration.SalesforceSourceProperties;
import software.amazon.customerprofiles.integration.ScheduledTriggerProperties;
import software.amazon.customerprofiles.integration.ServiceNowSourceProperties;
import software.amazon.customerprofiles.integration.SourceConnectorProperties;
import software.amazon.customerprofiles.integration.SourceFlowConfig;
import software.amazon.customerprofiles.integration.Task;
import software.amazon.customerprofiles.integration.TaskPropertiesMap;
import software.amazon.customerprofiles.integration.TriggerConfig;
import software.amazon.customerprofiles.integration.TriggerProperties;
import software.amazon.customerprofiles.integration.ZendeskSourceProperties;

public class TestUtils {

    public static FlowDefinition getValidFlowDefinition(String connectorType) {
        return FlowDefinition.builder()
                .flowName("flow")
                .description("description")
                .kmsArn("arn")
                .sourceFlowConfig(SourceFlowConfig.builder()
                        .connectorType(connectorType)
                        .connectorProfileName("test")
                        .incrementalPullConfig(IncrementalPullConfig.builder()
                                .datetimeTypeFieldName("LastModifiedDate")
                                .build())
                        .sourceConnectorProperties(getValidSourceConnectorProperties(connectorType))
                        .build())
                .triggerConfig(getValidTriggerConfig())
                .tasks(getValidTasks(connectorType))
                .build();
    }

    public static SourceConnectorProperties getValidSourceConnectorProperties(String connectorType) {
        switch (connectorType) {
            case "Marketo":
                return SourceConnectorProperties.builder()
                        .marketo(MarketoSourceProperties.builder()
                                .object("testObject")
                                .build())
                        .build();
            case "Salesforce":
                return SourceConnectorProperties.builder()
                        .salesforce(SalesforceSourceProperties.builder()
                                .enableDynamicFieldUpdate(false)
                                .includeDeletedRecords(false)
                                .object("testObject")
                                .build())
                        .build();
            case "S3":
                return SourceConnectorProperties.builder()
                        .s3(S3SourceProperties.builder()
                                .bucketName("testBucketName")
                                .bucketPrefix("testBucketPrefix")
                                .build())
                        .build();
            case "ServiceNow":
                return SourceConnectorProperties.builder()
                        .serviceNow(ServiceNowSourceProperties.builder()
                                .object("testObject")
                                .build())
                        .build();
            case "Zendesk":
                return SourceConnectorProperties.builder()
                        .zendesk(ZendeskSourceProperties.builder()
                                .object("testObject")
                                .build())
                        .build();
            default:
                return SourceConnectorProperties.builder().build();
        }
    }

    public static TriggerConfig getValidTriggerConfig() {
        return TriggerConfig.builder()
                .triggerType("Scheduled")
                .triggerProperties(
                        TriggerProperties.builder()
                                .scheduled(ScheduledTriggerProperties.builder()
                                        .firstExecutionFrom(Double.valueOf("000"))
                                        .scheduleStartTime(Double.valueOf("123"))
                                        .scheduleEndTime(Double.valueOf("456"))
                                        .dataPullMode("Incremental")
                                        .scheduleExpression("rate(1hours)")
                                        .scheduleOffset(1000)
                                        .timezone("UTC")
                                        .build())
                                .build())
                .build();
    }

    public static List<Task> getValidTasks(String connectorType) {
        return Collections.singletonList(
                Task.builder()
                        .taskType("Filter")
                        .connectorOperator(getValidConnectorOperator(connectorType))
                        .destinationField("destField")
                        .sourceFields(Arrays.asList("sourceField"))
                        .taskProperties(Collections.singletonList(
                                TaskPropertiesMap.builder()
                                        .operatorPropertyKey("SOURCE_DATA_TYPE")
                                        .property("string")
                                        .build()))
                        .build());
    }

    public static ConnectorOperator getValidConnectorOperator(String connectorType) {
        String operator = "PROJECTION";
        switch (connectorType) {
            case "Marketo":
                return ConnectorOperator.builder().marketo(operator).build();
            case "Salesforce":
                return ConnectorOperator.builder().salesforce(operator).build();
            case "S3":
                return ConnectorOperator.builder().s3(operator).build();
            case "ServiceNow":
                return ConnectorOperator.builder().serviceNow(operator).build();
            case "Zendesk":
                return ConnectorOperator.builder().zendesk(operator).build();
            default:
                return ConnectorOperator.builder().build();
        }
    }
}
