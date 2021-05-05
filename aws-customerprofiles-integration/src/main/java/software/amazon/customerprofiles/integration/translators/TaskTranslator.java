package software.amazon.customerprofiles.integration.translators;

import com.amazonaws.util.CollectionUtils;
import com.amazonaws.util.StringUtils;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import software.amazon.awssdk.services.customerprofiles.model.OperatorPropertiesKeys;
import software.amazon.awssdk.services.customerprofiles.model.Task;
import software.amazon.customerprofiles.integration.TaskPropertiesMap;
import static software.amazon.customerprofiles.integration.Translator.getTranslator;

public class TaskTranslator {

    public static List<Task> toServiceTasks(List<software.amazon.customerprofiles.integration.Task> model, String connectorType) {
        return CollectionUtils.isNullOrEmpty(model)
                ? Collections.emptyList()
                : model.stream().map(t -> toServiceTask(t, connectorType)).collect(Collectors.toList());
    }

    public static Task toServiceTask(software.amazon.customerprofiles.integration.Task model, String connectorType) {
        return Task.builder()
                .connectorOperator(getTranslator(connectorType).toServiceConnectorOperator(model.getConnectorOperator()))
                .destinationField(!StringUtils.isNullOrEmpty(model.getDestinationField()) ? model.getDestinationField() : null)
                .sourceFields(model.getSourceFields())
                .taskType(model.getTaskType())
                .taskProperties(CollectionUtils.isNullOrEmpty(model.getTaskProperties()) ? null :
                        model.getTaskProperties()
                                .stream()
                                .collect(Collectors.toMap(t -> OperatorPropertiesKeys.fromValue(
                                        t.getOperatorPropertyKey()), TaskPropertiesMap::getProperty)))
                .build();
    }
}
