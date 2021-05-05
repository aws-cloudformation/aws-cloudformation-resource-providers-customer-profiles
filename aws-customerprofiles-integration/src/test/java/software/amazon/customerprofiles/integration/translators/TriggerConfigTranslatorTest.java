package software.amazon.customerprofiles.integration.translators;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.customerprofiles.model.TriggerConfig;
import software.amazon.awssdk.services.customerprofiles.model.TriggerType;
import software.amazon.awssdk.services.customerprofiles.model.ScheduledTriggerProperties;
import software.amazon.customerprofiles.integration.TriggerProperties;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static software.amazon.customerprofiles.integration.translators.TestUtils.getValidTriggerConfig;

public class TriggerConfigTranslatorTest {

    @Test
    public void testToServiceTriggerConfig() {
        software.amazon.customerprofiles.integration.TriggerConfig model = getValidTriggerConfig();
        TriggerConfig translated = TriggerConfigTranslator.toServiceTriggerConfig(model);

        assertNotNull(translated);
        assertNotNull(translated.triggerType());
        assertEquals(translated.triggerType(), TriggerType.SCHEDULED);
        assertNotNull(translated.triggerProperties());
        assertNotNull(translated.triggerProperties().scheduled());
    }

    @Test
    public void testToServiceTriggerConfigNoTriggerProperties() {
        software.amazon.customerprofiles.integration.TriggerConfig model = getValidTriggerConfig();
        model.setTriggerProperties(null);
        TriggerConfig translated = TriggerConfigTranslator.toServiceTriggerConfig(model);

        assertNotNull(translated);
        assertNotNull(translated.triggerType());
        assertEquals(translated.triggerType(), TriggerType.SCHEDULED);
        assertNull(translated.triggerProperties());
    }

    @Test
    public void testToServiceTriggerConfigNoScheduledTriggerProperties() {
        software.amazon.customerprofiles.integration.TriggerConfig model = getValidTriggerConfig();

        TriggerProperties triggerProperties = TriggerProperties.builder().scheduled(null).build();
        model.setTriggerProperties(triggerProperties);
        TriggerConfig translated = TriggerConfigTranslator.toServiceTriggerConfig(model);

        assertNotNull(translated);
        assertNotNull(translated.triggerType());
        assertEquals(translated.triggerType(), TriggerType.SCHEDULED);
        assertNotNull(translated.triggerProperties());
        assertNull(translated.triggerProperties().scheduled());

    }

    @Test
    public void testToServiceTriggerConfigOnlyRequiredTriggerProperties() {
        software.amazon.customerprofiles.integration.TriggerConfig model = getValidTriggerConfig();
        software.amazon.customerprofiles.integration.ScheduledTriggerProperties props =
                software.amazon.customerprofiles.integration.ScheduledTriggerProperties.builder()
                .firstExecutionFrom(null)
                .scheduleStartTime(null)
                .scheduleEndTime(null)
                .scheduleExpression("rate(1hours)")
                .timezone(null)
                .dataPullMode(null)
                .scheduleOffset(null)
                .build();

        model.setTriggerProperties(software.amazon.customerprofiles.integration.TriggerProperties.builder().scheduled(props).build());
        TriggerConfig translated = TriggerConfigTranslator.toServiceTriggerConfig(model);

        assertNotNull(translated);
        assertNotNull(translated.triggerProperties());
        assertNotNull(translated.triggerProperties().scheduled());
        ScheduledTriggerProperties scheduledProps = translated.triggerProperties().scheduled();
        assertNull(scheduledProps.dataPullMode());
        assertNull(scheduledProps.firstExecutionFrom());
        assertNull(scheduledProps.scheduleStartTime());
        assertNull(scheduledProps.scheduleEndTime());
        assertNull(scheduledProps.scheduleOffset());
        assertNull(scheduledProps.timezone());
    }
}
