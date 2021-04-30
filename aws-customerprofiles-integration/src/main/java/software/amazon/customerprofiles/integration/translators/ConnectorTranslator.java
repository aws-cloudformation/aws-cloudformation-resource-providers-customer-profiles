package software.amazon.customerprofiles.integration.translators;

import software.amazon.awssdk.services.customerprofiles.model.ConnectorOperator;
import software.amazon.awssdk.services.customerprofiles.model.SourceFlowConfig;

public interface ConnectorTranslator {
    SourceFlowConfig toServiceSourceFlowConfig(software.amazon.customerprofiles.integration.SourceFlowConfig model);
    ConnectorOperator toServiceConnectorOperator(software.amazon.customerprofiles.integration.ConnectorOperator model);
}
