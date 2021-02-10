package software.amazon.customerprofiles.objecttype;

import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Translator {

    static String ARN_FORMAT = "arn:%s:profile:%s:%s:domains/%s/object-types/%s";

    static String toProfileObjectTypeARN(final ResourceHandlerRequest<ResourceModel> request) {
        return String.format(ARN_FORMAT, request.getAwsPartition(), request.getRegion(), request.getAwsAccountId(),
                request.getDesiredResourceState().getDomainName(), request.getDesiredResourceState().getObjectTypeName());
    }

    static List<Tag> mapTagsToList(Map<String, String> tags) {
        if (tags.isEmpty()) {
            return null;
        }
        return tags.entrySet().stream()
                .map(t -> Tag.builder()
                        .key(t.getKey())
                        .value(t.getValue()).build())
                .collect(Collectors.toList());
    }

    static Map<String, software.amazon.awssdk.services.customerprofiles.model.ObjectTypeField> listFieldsToMap(List<FieldMap> fieldMaps) {
        if (fieldMaps == null || fieldMaps.isEmpty()) {
            return null;
        }
        Map<String, software.amazon.awssdk.services.customerprofiles.model.ObjectTypeField> fields = new HashMap<>();
        fieldMaps.forEach(each -> fields.put(each.getName(), software.amazon.awssdk.services.customerprofiles.model.ObjectTypeField.builder()
                .contentType(each.getObjectTypeField().getContentType())
                .source(each.getObjectTypeField().getSource())
                .target(each.getObjectTypeField().getTarget())
                .build()));
        return fields;
    }

    static List<FieldMap> mapFieldsToList(Map<String, software.amazon.awssdk.services.customerprofiles.model.ObjectTypeField> mapField) {
        if (mapField.isEmpty()) {
            return null;
        }

        List<FieldMap> fieldMaps = new ArrayList<>();
        mapField.forEach((name, objectTypeField) -> fieldMaps.add(FieldMap.builder()
                .name(name)
                .objectTypeField(ObjectTypeField.builder()
                        .source(objectTypeField.source())
                        .contentType(objectTypeField.contentTypeAsString())
                        .target(objectTypeField.target())
                        .build())
                .build()));

        return fieldMaps;
    }

    static Map<String, List<software.amazon.awssdk.services.customerprofiles.model.ObjectTypeKey>> listKeysToMap(List<KeyMap> keyMaps) {
        if (keyMaps == null || keyMaps.isEmpty()) {
            return null;
        }
        Map<String, List<software.amazon.awssdk.services.customerprofiles.model.ObjectTypeKey>> keys = new HashMap<>();
        for (KeyMap keyMap: keyMaps) {
            String name = keyMap.getName();
            List<software.amazon.awssdk.services.customerprofiles.model.ObjectTypeKey> objectTypeKeyList = new ArrayList<>();
            for (ObjectTypeKey objectTypeKey : keyMap.getObjectTypeKeyList()) {
                objectTypeKeyList.add(software.amazon.awssdk.services.customerprofiles.model.ObjectTypeKey.builder()
                        .fieldNames(objectTypeKey.getFieldNames())
                        .standardIdentifiersWithStrings(objectTypeKey.getStandardIdentifiers())
                        .build());
            }
            keys.put(name, objectTypeKeyList);
        }
        return keys;
    }

    static List<KeyMap> mapKeysToList(Map<String, List<software.amazon.awssdk.services.customerprofiles.model.ObjectTypeKey>> mapKey) {
        if (mapKey.isEmpty()) {
            return null;
        }
        List<KeyMap> keyMaps = new ArrayList<>();
        for (Map.Entry<String, List<software.amazon.awssdk.services.customerprofiles.model.ObjectTypeKey>> keyEntry : mapKey.entrySet()) {
            String name = keyEntry.getKey();
            List<ObjectTypeKey> objectTypeKeyList = new ArrayList<>();
            for (software.amazon.awssdk.services.customerprofiles.model.ObjectTypeKey objectTypeKey : keyEntry.getValue()) {
                objectTypeKeyList.add(ObjectTypeKey.builder()
                        .fieldNames(objectTypeKey.fieldNames())
                        .standardIdentifiers(objectTypeKey.standardIdentifiersAsStrings())
                        .build());
            }
            keyMaps.add(KeyMap.builder()
                    .name(name)
                    .objectTypeKeyList(objectTypeKeyList)
                    .build());
        }
        return keyMaps;
    }
}
