package software.amazon.customerprofiles.integration.translators;

import com.amazonaws.util.StringUtils;
import software.amazon.awssdk.services.customerprofiles.model.ConnectorOperator;
import software.amazon.awssdk.services.customerprofiles.model.IncrementalPullConfig;
import software.amazon.awssdk.services.customerprofiles.model.ZendeskSourceProperties;
import software.amazon.awssdk.services.customerprofiles.model.SourceConnectorProperties;
import software.amazon.awssdk.services.customerprofiles.model.SourceFlowConfig;
import software.amazon.customerprofiles.integration.translators.ConnectorTranslator;

public class ZendeskTranslator implements ConnectorTranslator {

    @Override
    public SourceFlowConfig toServiceSourceFlowConfig(software.amazon.customerprofiles.integration.SourceFlowConfig model) {
        return SourceFlowConfig.builder()
                .connectorProfileName(StringUtils.isNullOrEmpty(model.getConnectorProfileName()) ? null :
                        model.getConnectorProfileName())
                .connectorType(model.getConnectorType())
                .sourceConnectorProperties(
                        SourceConnectorProperties.builder()
                                .zendesk(toServiceZendeskSourceProperties(model
                                        .getSourceConnectorProperties()
                                        .getZendesk()))
                                .build()
                )
                .build();
    }

    private ZendeskSourceProperties toServiceZendeskSourceProperties(software.amazon.customerprofiles.integration.ZendeskSourceProperties model) {
        return ZendeskSourceProperties.builder()
                .object(model.getObject())
                .build();
    }

    @Override
    public ConnectorOperator toServiceConnectorOperator(software.amazon.customerprofiles.integration.ConnectorOperator model) {
        if (model == null) {
            return null;
        }
        return ConnectorOperator.builder()
                .zendesk(model.getZendesk())
                .build();
    }
}
