package software.amazon.customerprofiles.integration.translators;

import com.amazonaws.util.StringUtils;
import software.amazon.awssdk.services.customerprofiles.model.ConnectorOperator;
import software.amazon.awssdk.services.customerprofiles.model.IncrementalPullConfig;
import software.amazon.awssdk.services.customerprofiles.model.ServiceNowSourceProperties;
import software.amazon.awssdk.services.customerprofiles.model.SourceConnectorProperties;
import software.amazon.awssdk.services.customerprofiles.model.SourceFlowConfig;
import software.amazon.customerprofiles.integration.translators.ConnectorTranslator;

public class ServiceNowTranslator implements ConnectorTranslator {

    @Override
    public SourceFlowConfig toServiceSourceFlowConfig(software.amazon.customerprofiles.integration.SourceFlowConfig model) {
        return SourceFlowConfig.builder()
                .connectorProfileName(StringUtils.isNullOrEmpty(model.getConnectorProfileName()) ? null :
                        model.getConnectorProfileName())
                .connectorType(model.getConnectorType())
                .sourceConnectorProperties(
                        SourceConnectorProperties.builder()
                                .serviceNow(toServiceServiceNowSourceProperties(model
                                        .getSourceConnectorProperties()
                                        .getServiceNow()))
                                .build()
                )
                .build();
    }

    private ServiceNowSourceProperties toServiceServiceNowSourceProperties(software.amazon.customerprofiles.integration.ServiceNowSourceProperties model) {
        return ServiceNowSourceProperties.builder()
                .object(model.getObject())
                .build();
    }

    @Override
    public ConnectorOperator toServiceConnectorOperator(software.amazon.customerprofiles.integration.ConnectorOperator model) {
        if (model == null) {
            return null;
        }
        return ConnectorOperator.builder()
                .serviceNow(model.getServiceNow())
                .build();
    }
}
