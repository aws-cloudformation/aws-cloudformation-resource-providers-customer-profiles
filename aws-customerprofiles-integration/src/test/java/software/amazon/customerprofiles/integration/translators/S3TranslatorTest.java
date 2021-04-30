package software.amazon.customerprofiles.integration.translators;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.customerprofiles.model.ConnectorOperator;
import software.amazon.awssdk.services.customerprofiles.model.SourceFlowConfig;
import software.amazon.customerprofiles.integration.IncrementalPullConfig;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static software.amazon.customerprofiles.integration.translators.TestUtils.getValidSourceConnectorProperties;

public class S3TranslatorTest {

    S3Translator translator = new S3Translator();

    @Test
    public void testSourceFlowConfig() {
        software.amazon.customerprofiles.integration.SourceFlowConfig model = software.amazon.customerprofiles.integration.SourceFlowConfig.builder()
                .connectorProfileName("test name")
                .connectorType("S3")
                .sourceConnectorProperties(getValidSourceConnectorProperties("S3"))
                .build();
        SourceFlowConfig translated = translator.toServiceSourceFlowConfig(model);

        assertNotNull(translated);
        assertNotNull(translated.connectorProfileName());
        assertNotNull(translated.connectorType());
        assertNotNull(translated.sourceConnectorProperties().s3().bucketName());
        assertNotNull(translated.sourceConnectorProperties().s3().bucketPrefix());
    }

    @Test
    public void testSourceFlowConfigOnlyRequiredFields() {
        software.amazon.customerprofiles.integration.SourceFlowConfig model = software.amazon.customerprofiles.integration.SourceFlowConfig.builder()
                .connectorType("S3")
                .sourceConnectorProperties(getValidSourceConnectorProperties("S3"))
                .build();
        SourceFlowConfig translated = translator.toServiceSourceFlowConfig(model);

        assertNotNull(translated);
        assertNull(translated.connectorProfileName());
        assertNull(translated.incrementalPullConfig());
    }

    @Test
    public void testConnectorOperator() {
        ConnectorOperator translated = translator.toServiceConnectorOperator(
                software.amazon.customerprofiles.integration.ConnectorOperator.builder()
                        .s3("test")
                        .build());

        assertNotNull(translated);
        assertNotNull(translated.s3());
    }

    @Test
    public void testConnectorOperatorNull() {
        ConnectorOperator translated = translator.toServiceConnectorOperator(null);
        assertNull(translated);
    }
}
