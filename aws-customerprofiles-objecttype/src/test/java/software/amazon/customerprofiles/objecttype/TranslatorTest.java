package software.amazon.customerprofiles.objecttype;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class TranslatorTest {

    @Test
    public void listFieldsToMap_fieldMapsIsEmpty() {
        assertThat(Translator.listFieldsToMap(Lists.newArrayList())).isNull();
    }

    @Test
    public void listFieldsToMap_fieldMapsIsNull() {
        assertThat(Translator.listFieldsToMap(null)).isNull();
    }

    @Test
    public void mapFieldsToList_mapFieldIsEmpty() {
        assertThat(Translator.mapFieldsToList(ImmutableMap.of())).isNull();
    }

    @Test
    public void listKeysToMap_keyMapsIsEmpty() {
        assertThat(Translator.listKeysToMap(Lists.newArrayList())).isNull();
    }

    @Test
    public void listKeysToMap_keyMapsIsNull() {
        assertThat(Translator.listKeysToMap(null)).isNull();
    }

    @Test
    public void mapFieldsToList_mapKeyIsEmpty() {
        assertThat(Translator.mapFieldsToList(ImmutableMap.of())).isNull();
    }
}
