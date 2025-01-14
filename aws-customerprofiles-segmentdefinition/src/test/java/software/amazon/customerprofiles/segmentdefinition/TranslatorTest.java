package software.amazon.customerprofiles.segmentdefinition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.customerprofiles.model.ProfileAttributes;
import software.amazon.awssdk.services.customerprofiles.model.ProfileDimension;
import software.amazon.awssdk.services.customerprofiles.model.SegmentGroup;
import software.amazon.awssdk.services.customerprofiles.model.Group;
import software.amazon.awssdk.services.customerprofiles.model.SourceSegment;
import software.amazon.awssdk.services.customerprofiles.model.Dimension;

class TranslatorTest {

  @Test
  void testTranslateFromInternalSegmentGroup_Null() {
    SegmentGroup result = Translator.translateFromInternalSegmentGroup(null);
    assertNull(result);
  }

  @Test
  void testTranslateFromInternalSegmentGroup_Valid() {
    SegmentGroup result = Translator.translateFromInternalSegmentGroup(createInternalSegmentGroup());

    assertNotNull(result);
    assertEquals(1, result.groups().size());
    assertEquals(1, result.groups().get(0).sourceSegments().size());
    assertEquals("SegmentDef1", result.groups().get(0).sourceSegments().get(0).segmentDefinitionName());
    assertEquals(1, result.groups().get(0).dimensions().size());
    Dimension dimension = result.groups().get(0).dimensions().get(0);
    assertNotNull(dimension.profileAttributes());
    assertNotNull(dimension.profileAttributes().accountNumber());
    assertEquals("INCLUSIVE", dimension.profileAttributes().accountNumber().dimensionType().toString());
    assertEquals("123456789", dimension.profileAttributes().accountNumber().values().get(0));
  }

  @Test
  void testTranslateToInternalSegmentGroup_Null() {
    software.amazon.customerprofiles.segmentdefinition.SegmentGroup result =
        Translator.translateToInternalSegmentGroup(null);
    assertNull(result);
  }

  @Test
  void testTranslateToInternalSegmentGroup_Valid() {
    software.amazon.customerprofiles.segmentdefinition.SegmentGroup result =
        Translator.translateToInternalSegmentGroup(createSegmentGroup());

    assertNotNull(result);
    assertEquals(1, result.getGroups().size());
    assertEquals("INCLUSIVE", result.getGroups().get(0).getDimensions().get(0).getProfileAttributes().getAccountNumber().getDimensionType().toString());
    assertEquals(1, result.getGroups().get(0).getSourceSegments().size());
    assertEquals("SegmentDef1", result.getGroups().get(0).getSourceSegments().get(0).getSegmentDefinitionName());
  }

  // Helper methods to create dummy data for testing
  private software.amazon.customerprofiles.segmentdefinition.SegmentGroup createInternalSegmentGroup() {
    return software.amazon.customerprofiles.segmentdefinition.SegmentGroup.builder()
        .groups(Collections.singletonList(
            software.amazon.customerprofiles.segmentdefinition.Group.builder()
                .sourceSegments(Collections.singletonList(
                    software.amazon.customerprofiles.segmentdefinition.SourceSegment.builder()
                        .segmentDefinitionName("SegmentDef1")
                        .build()
                ))
                .dimensions(Collections.singletonList(
                    software.amazon.customerprofiles.segmentdefinition.Dimension.builder()
                        .profileAttributes(
                            software.amazon.customerprofiles.segmentdefinition.ProfileAttributes.builder()
                                .accountNumber(
                                    software.amazon.customerprofiles.segmentdefinition.ProfileDimension.builder()
                                        .dimensionType("INCLUSIVE")
                                        .values(Collections.singletonList("123456789"))
                                        .build()
                                )
                                .build()
                        )
                        .build()
                ))
                .build()
        ))
        .include("ALL")
        .build();
  }

  private SegmentGroup createSegmentGroup() {
    return SegmentGroup.builder()
        .groups(Collections.singletonList(
            Group.builder()
                .dimensions(Collections.singletonList(
                    Dimension.builder()
                        .profileAttributes(
                            ProfileAttributes.builder()
                                .accountNumber(
                                    ProfileDimension.builder()
                                        .dimensionType("INCLUSIVE")
                                        .values(Collections.singletonList("123456789"))
                                        .build()
                                )
                                .build()
                        )
                        .build()
                ))
                .sourceSegments(Collections.singletonList(
                    SourceSegment.builder()
                        .segmentDefinitionName("SegmentDef1")
                        .build()
                ))
                .sourceType("ALL")
                .type("ALL")
                .build()
        ))
        .include("ALL")
        .build();
  }
}