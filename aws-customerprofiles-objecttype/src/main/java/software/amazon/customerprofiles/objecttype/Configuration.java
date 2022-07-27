package software.amazon.customerprofiles.objecttype;

import java.util.Map;
import java.util.stream.Collectors;

class Configuration extends BaseConfiguration {

    public Configuration() {
        super("aws-customerprofiles-objecttype.json");
    }

    /**
     * Providers should implement this method if their resource has a 'Tags' property to define resource-level tags
     * @return tags
     */
    @Override
    public Map<String, String> resourceDefinedTags(final ResourceModel resourceModel) {
        if (resourceModel.getTags() == null) {
            return null;
        } else {
            return resourceModel.getTags().stream().collect(Collectors.toMap(Tag::getKey, Tag::getValue, (value1, value2) -> value2));
        }
    }
}
