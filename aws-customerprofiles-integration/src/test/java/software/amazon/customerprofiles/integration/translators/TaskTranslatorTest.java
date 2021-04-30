package software.amazon.customerprofiles.integration.translators;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import software.amazon.awssdk.services.customerprofiles.model.Task;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static software.amazon.customerprofiles.integration.translators.TestUtils.getValidTasks;

public class TaskTranslatorTest {

    TaskTranslator translator = new TaskTranslator();

    private static Stream<String> getConnectors() {
        return Stream.of("Salesforce", "S3", "Zendesk", "Marketo", "ServiceNow");
    }

    @ParameterizedTest
    @MethodSource("getConnectors")
    public void testToServiceTasks(String connectorType) {
        List<software.amazon.customerprofiles.integration.Task> modelTasks = getValidTasks(connectorType);
        List<Task> tasks = TaskTranslator.toServiceTasks(modelTasks, connectorType);

        assertNotNull(tasks);
        for (Task t : tasks) {
            assertNotNull(t.connectorOperator());
            assertNotNull(t.destinationField());
        }
    }

    @Test
    public void testToServiceTasksNull() {
        List<Task> tasks = TaskTranslator.toServiceTasks(null, null);
        assertEquals(tasks, Collections.emptyList());
    }

    @Test
    public void testTaskNullFields() {
        // only TaskType and SourceFields are required non-null values
        software.amazon.customerprofiles.integration.Task taskWithNullFields =
                software.amazon.customerprofiles.integration.Task.builder()
                    .taskType("Filter")
                    .connectorOperator(null)
                    .destinationField(null)
                    .sourceFields(Arrays.asList("field"))
                    .taskProperties(null)
                    .build();

        List<Task> tasks = TaskTranslator.toServiceTasks(Collections.singletonList(taskWithNullFields),
                "Salesforce");

        for (Task t : tasks) {
            assertNull(t.connectorOperator());
            assertNull(t.destinationField());
            assertTrue(t.taskProperties().isEmpty());
        }
    }
}
