package software.amazon.customerprofiles.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.customerprofiles.model.FlowDefinition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static software.amazon.customerprofiles.integration.translators.TestUtils.getValidFlowDefinition;

@ExtendWith(MockitoExtension.class)
public class TranslatorTest {
    @Test
    public void test() {
        assertThat(Translator.mapTagsToList(null)).isNull();
    }

    @Test
    public void testBuildFlowDefinition() {
        FlowDefinition flowDefinition = Translator.buildServiceFlowDefinition(getValidFlowDefinition("Salesforce"));

        assertNotNull(flowDefinition);
        assertNotNull(flowDefinition.description());
        assertNotNull(flowDefinition.flowName());
        assertNotNull(flowDefinition.kmsArn());
        assertNotNull(flowDefinition.sourceFlowConfig());
        assertNotNull(flowDefinition.tasks());
        assertNotNull(flowDefinition.triggerConfig());
    }

    @Test
    public void testBuildFlowDefinitionEmptyDescription() {
        software.amazon.customerprofiles.integration.FlowDefinition model = getValidFlowDefinition("Salesforce");
        model.setDescription("");
        FlowDefinition flowDefinition = Translator.buildServiceFlowDefinition(model);

        assertNotNull(flowDefinition);
        assertNull(flowDefinition.description());
        assertNotNull(flowDefinition.flowName());
        assertNotNull(flowDefinition.kmsArn());
        assertNotNull(flowDefinition.sourceFlowConfig());
        assertNotNull(flowDefinition.tasks());
        assertNotNull(flowDefinition.triggerConfig());
    }
}
