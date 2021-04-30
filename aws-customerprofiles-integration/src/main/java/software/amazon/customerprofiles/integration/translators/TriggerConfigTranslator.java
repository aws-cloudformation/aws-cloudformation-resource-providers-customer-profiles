package software.amazon.customerprofiles.integration.translators;

import java.time.Instant;
import software.amazon.awssdk.services.customerprofiles.model.ScheduledTriggerProperties;
import software.amazon.awssdk.services.customerprofiles.model.TriggerConfig;
import software.amazon.awssdk.services.customerprofiles.model.TriggerProperties;

public class TriggerConfigTranslator {

    public static TriggerConfig toServiceTriggerConfig(software.amazon.customerprofiles.integration.TriggerConfig model) {
        return TriggerConfig.builder()
                .triggerType(model.getTriggerType())
                .triggerProperties(model.getTriggerProperties() == null ? null :
                        TriggerProperties.builder()
                                .scheduled(toServiceScheduledTriggerProperties(model.getTriggerProperties().getScheduled()))
                                .build())
                .build();
    }

    private static ScheduledTriggerProperties toServiceScheduledTriggerProperties(software.amazon.customerprofiles.integration.ScheduledTriggerProperties model) {
        if (model == null) {
            return null;
        }

        return ScheduledTriggerProperties.builder()
                .dataPullMode(model.getDataPullMode())
                .firstExecutionFrom(model.getFirstExecutionFrom() == null ? null : Instant.ofEpochSecond(
                        model.getFirstExecutionFrom().longValue()))
                .scheduleStartTime(model.getScheduleStartTime() == null ? null : Instant.ofEpochSecond(
                        model.getScheduleStartTime().longValue()))
                .scheduleEndTime(model.getScheduleEndTime() == null ? null : Instant.ofEpochSecond(
                        model.getScheduleEndTime().longValue()))
                .scheduleExpression(model.getScheduleExpression())
                .scheduleOffset(model.getScheduleOffset() == null ? null : model.getScheduleOffset().longValue())
                .timezone(model.getTimezone())
                .build();
    }
}
