package software.amazon.customerprofiles.integration.translators;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.customerprofiles.model.ConnectorOperator;
import software.amazon.awssdk.services.customerprofiles.model.SourceFlowConfig;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.customerprofiles.integration.IncrementalPullConfig;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static software.amazon.customerprofiles.integration.translators.TestUtils.getValidSourceConnectorProperties;

public class SalesforceTranslatorTest {

    SalesforceTranslator translator = new SalesforceTranslator();

    @Test
    public void testSourceFlowConfig() {
        software.amazon.customerprofiles.integration.SourceFlowConfig model = software.amazon.customerprofiles.integration.SourceFlowConfig.builder()
                .connectorProfileName("test name")
                .connectorType("Salesforce")
                .incrementalPullConfig(IncrementalPullConfig.builder()
                        .datetimeTypeFieldName("LastModifiedDate")
                        .build())
                .sourceConnectorProperties(getValidSourceConnectorProperties("Salesforce"))
                .build();
        SourceFlowConfig translated = translator.toServiceSourceFlowConfig(model);

        assertNotNull(translated);
        assertNotNull(translated.connectorProfileName());
        assertNotNull(translated.connectorType());
        assertNotNull(translated.incrementalPullConfig().datetimeTypeFieldName());
        assertNotNull(translated.sourceConnectorProperties().salesforce().object());
        assertNotNull(translated.sourceConnectorProperties().salesforce().enableDynamicFieldUpdate());
        assertNotNull(translated.sourceConnectorProperties().salesforce().includeDeletedRecords());
    }

    @Test
    public void testSourceFlowConfigOnlyRequiredFields() {
        software.amazon.customerprofiles.integration.SourceFlowConfig model = software.amazon.customerprofiles.integration.SourceFlowConfig.builder()
                .connectorType("Salesforce")
                .sourceConnectorProperties(getValidSourceConnectorProperties("Salesforce"))
                .build();
        SourceFlowConfig translated = translator.toServiceSourceFlowConfig(model);

        assertNotNull(translated);
        assertNull(translated.connectorProfileName());
        assertNull(translated.incrementalPullConfig());
    }

    @Test
    public void testSourcePropertiesInvalid() {
        software.amazon.customerprofiles.integration.SourceFlowConfig model = software.amazon.customerprofiles.integration.SourceFlowConfig.builder()
                .connectorType("Salesforce")
                .sourceConnectorProperties(getValidSourceConnectorProperties("Marketo"))
                .build();
        assertThrows(CfnInvalidRequestException.class, () -> translator.toServiceSourceFlowConfig(model));
    }

    @Test
    public void testConnectorOperator() {
        ConnectorOperator translated = translator.toServiceConnectorOperator(
                software.amazon.customerprofiles.integration.ConnectorOperator.builder()
                        .salesforce("test")
                        .build());

        assertNotNull(translated);
        assertNotNull(translated.salesforce());
    }

    @Test
    public void testConnectorOperatorNull() {
        ConnectorOperator translated = translator.toServiceConnectorOperator(null);
        assertNull(translated);
    }
}
