package software.amazon.customerprofiles.integration.translators;

import com.amazonaws.util.StringUtils;
import software.amazon.awssdk.services.customerprofiles.model.ConnectorOperator;
import software.amazon.awssdk.services.customerprofiles.model.IncrementalPullConfig;
import software.amazon.awssdk.services.customerprofiles.model.SalesforceSourceProperties;
import software.amazon.awssdk.services.customerprofiles.model.SourceConnectorProperties;
import software.amazon.awssdk.services.customerprofiles.model.SourceFlowConfig;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;

public class SalesforceTranslator implements ConnectorTranslator {

    @Override
    public SourceFlowConfig toServiceSourceFlowConfig(software.amazon.customerprofiles.integration.SourceFlowConfig model) {
        return SourceFlowConfig.builder()
                .connectorProfileName(StringUtils.isNullOrEmpty(model.getConnectorProfileName()) ? null :
                        model.getConnectorProfileName())
                .connectorType(model.getConnectorType())
                .incrementalPullConfig(model.getIncrementalPullConfig() == null ? null : IncrementalPullConfig.builder()
                        .datetimeTypeFieldName(model.getIncrementalPullConfig().getDatetimeTypeFieldName())
                        .build())
                .sourceConnectorProperties(
                        SourceConnectorProperties.builder()
                                .salesforce(toServiceSourceProperties(model
                                        .getSourceConnectorProperties()
                                        .getSalesforce()))
                                .build()
                )
                .build();
    }

    private SalesforceSourceProperties toServiceSourceProperties(software.amazon.customerprofiles.integration.SalesforceSourceProperties model) {
        if (model == null) {
            throw new CfnInvalidRequestException("SourceConnectorType and SourceConnectorProperties must be same type.");
        }
        return SalesforceSourceProperties.builder()
                .enableDynamicFieldUpdate(model.getEnableDynamicFieldUpdate())
                .includeDeletedRecords(model.getIncludeDeletedRecords())
                .object(model.getObject())
                .build();
    }

    @Override
    public ConnectorOperator toServiceConnectorOperator(software.amazon.customerprofiles.integration.ConnectorOperator model) {
        if (model == null) {
            return null;
        }
        return ConnectorOperator.builder()
                .salesforce(model.getSalesforce())
                .build();
    }
}
