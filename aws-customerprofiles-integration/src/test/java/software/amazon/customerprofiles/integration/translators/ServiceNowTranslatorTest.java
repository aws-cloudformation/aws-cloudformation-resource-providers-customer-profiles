package software.amazon.customerprofiles.integration.translators;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.customerprofiles.model.ConnectorOperator;
import software.amazon.awssdk.services.customerprofiles.model.SourceFlowConfig;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static software.amazon.customerprofiles.integration.translators.TestUtils.getValidSourceConnectorProperties;

public class ServiceNowTranslatorTest {
    ServiceNowTranslator translator = new ServiceNowTranslator();

    @Test
    public void testSourceFlowConfig() {
        software.amazon.customerprofiles.integration.SourceFlowConfig model = software.amazon.customerprofiles.integration.SourceFlowConfig.builder()
                .connectorProfileName("test name")
                .connectorType("ServiceNow")
                .sourceConnectorProperties(getValidSourceConnectorProperties("ServiceNow"))
                .build();
        SourceFlowConfig translated = translator.toServiceSourceFlowConfig(model);

        assertNotNull(translated);
        assertNotNull(translated.connectorProfileName());
        assertNotNull(translated.connectorType());
        assertNotNull(translated.sourceConnectorProperties().serviceNow().object());
    }

    @Test
    public void testSourceFlowConfigOnlyRequiredFields() {
        software.amazon.customerprofiles.integration.SourceFlowConfig model = software.amazon.customerprofiles.integration.SourceFlowConfig.builder()
                .connectorType("ServiceNow")
                .sourceConnectorProperties(getValidSourceConnectorProperties("ServiceNow"))
                .build();
        SourceFlowConfig translated = translator.toServiceSourceFlowConfig(model);

        assertNotNull(translated);
        assertNull(translated.connectorProfileName());
        assertNull(translated.incrementalPullConfig());
    }

    @Test
    public void testSourcePropertiesInvalid() {
        software.amazon.customerprofiles.integration.SourceFlowConfig model = software.amazon.customerprofiles.integration.SourceFlowConfig.builder()
                .connectorType("ServiceNow")
                .sourceConnectorProperties(getValidSourceConnectorProperties("Salesforce"))
                .build();
        assertThrows(CfnInvalidRequestException.class, () -> translator.toServiceSourceFlowConfig(model));
    }

    @Test
    public void testConnectorOperator() {
        ConnectorOperator translated = translator.toServiceConnectorOperator(
                software.amazon.customerprofiles.integration.ConnectorOperator.builder()
                        .serviceNow("test")
                        .build());

        assertNotNull(translated);
        assertNotNull(translated.serviceNow());
    }

    @Test
    public void testConnectorOperatorNull() {
        ConnectorOperator translated = translator.toServiceConnectorOperator(null);
        assertNull(translated);
    }
}
