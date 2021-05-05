package software.amazon.customerprofiles.integration.translators;

import com.amazonaws.util.StringUtils;
import software.amazon.awssdk.services.customerprofiles.model.ConnectorOperator;
import software.amazon.awssdk.services.customerprofiles.model.MarketoSourceProperties;
import software.amazon.awssdk.services.customerprofiles.model.SourceConnectorProperties;
import software.amazon.awssdk.services.customerprofiles.model.SourceFlowConfig;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;

public class MarketoTranslator implements ConnectorTranslator {

    @Override
    public SourceFlowConfig toServiceSourceFlowConfig(software.amazon.customerprofiles.integration.SourceFlowConfig model) {
        return SourceFlowConfig.builder()
                .connectorProfileName(StringUtils.isNullOrEmpty(model.getConnectorProfileName()) ? null :
                        model.getConnectorProfileName())
                .connectorType(model.getConnectorType())
                .sourceConnectorProperties(
                        SourceConnectorProperties.builder()
                                .marketo(toServiceSourceProperties(model
                                        .getSourceConnectorProperties()
                                        .getMarketo()))
                                .build()
                )
                .build();
    }

    private MarketoSourceProperties toServiceSourceProperties(software.amazon.customerprofiles.integration.MarketoSourceProperties model) {
        if (model == null) {
            throw new CfnInvalidRequestException("SourceConnectorType and SourceConnectorProperties must be same type.");
        }
        return MarketoSourceProperties.builder()
                .object(model.getObject())
                .build();
    }

    @Override
    public ConnectorOperator toServiceConnectorOperator(software.amazon.customerprofiles.integration.ConnectorOperator model) {
        if (model == null) {
            return null;
        }
        return ConnectorOperator.builder()
                .marketo(model.getMarketo())
                .build();
    }
}
