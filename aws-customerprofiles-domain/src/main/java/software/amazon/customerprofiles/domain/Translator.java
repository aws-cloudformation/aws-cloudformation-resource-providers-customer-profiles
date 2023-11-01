package software.amazon.customerprofiles.domain;

import com.amazonaws.util.CollectionUtils;
import software.amazon.awssdk.services.customerprofiles.model.AttributeTypesSelector;
import software.amazon.awssdk.services.customerprofiles.model.AutoMerging;
import software.amazon.awssdk.services.customerprofiles.model.ConflictResolution;
import software.amazon.awssdk.services.customerprofiles.model.Consolidation;
import software.amazon.awssdk.services.customerprofiles.model.DomainStats;
import software.amazon.awssdk.services.customerprofiles.model.ExportingConfig;
import software.amazon.awssdk.services.customerprofiles.model.JobSchedule;
import software.amazon.awssdk.services.customerprofiles.model.MatchingRequest;
import software.amazon.awssdk.services.customerprofiles.model.MatchingResponse;
import software.amazon.awssdk.services.customerprofiles.model.MatchingRule;
import software.amazon.awssdk.services.customerprofiles.model.RuleBasedMatchingRequest;
import software.amazon.awssdk.services.customerprofiles.model.RuleBasedMatchingResponse;
import software.amazon.awssdk.services.customerprofiles.model.S3ExportingConfig;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Translator {

    static String ARN_FORMAT = "arn:%s:profile:%s:%s:domains/%s";

    static String toDomainARN(final ResourceHandlerRequest<ResourceModel> request) {
        return String.format(ARN_FORMAT, request.getAwsPartition(), request.getRegion(), request.getAwsAccountId(),
                request.getDesiredResourceState().getDomainName());
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

    public static software.amazon.customerprofiles.domain.DomainStats translateToInternalStats(DomainStats source) {
        if (source == null) {
            return null;
        }
        return software.amazon.customerprofiles.domain.DomainStats.builder()
            .meteringProfileCount(source.meteringProfileCount() == null ? null : Double.valueOf(source.meteringProfileCount()))
            .objectCount(source.objectCount() == null ? null : Double.valueOf(source.objectCount()))
            .profileCount(source.profileCount() == null ? null : Double.valueOf(source.profileCount()))
            .totalSize(source.totalSize() == null ? null : Double.valueOf(source.totalSize()))
            .build();
    }

    public static MatchingRequest buildServiceMatching(software.amazon.customerprofiles.domain.Matching model) {
        if (model == null) {
            return null;
        }
        return MatchingRequest.builder()
            .enabled(model.getEnabled())
            .autoMerging(toServiceAutoMerging(model.getAutoMerging()))
            .exportingConfig(toServiceExportingConfig(model.getExportingConfig()))
            .jobSchedule(toServiceJobSchedule(model.getJobSchedule()))
            .build();
    }

    public static RuleBasedMatchingRequest buildServiceRuleBasedMatching(software.amazon.customerprofiles.domain.RuleBasedMatching model) {
        if (model == null) {
            return null;
        }
        return RuleBasedMatchingRequest.builder()
            .enabled(model.getEnabled())
            .attributeTypesSelector(toServiceAttributeTypesSelector(model.getAttributeTypesSelector()))
            .conflictResolution(toServiceConflictResolution(model.getConflictResolution()))
            .exportingConfig(toServiceExportingConfig(model.getExportingConfig()))
            .matchingRules(toServiceMatchingRules(model.getMatchingRules()))
            .maxAllowedRuleLevelForMatching(model.getMaxAllowedRuleLevelForMatching())
            .maxAllowedRuleLevelForMerging(model.getMaxAllowedRuleLevelForMerging())
            .build();
    }

    public static software.amazon.customerprofiles.domain.Matching translateToInternalMatchingResponse(
        MatchingResponse source) {
        if (source == null) {
            return null;
        }

        return Matching.builder()
            .enabled(source.enabled())
            .autoMerging(source.autoMerging() == null ? null :
                software.amazon.customerprofiles.domain.AutoMerging.builder()
                    .enabled(source.autoMerging().enabled())
                    .conflictResolution(source.autoMerging().conflictResolution() == null ? null :
                        software.amazon.customerprofiles.domain.ConflictResolution.builder()
                            .conflictResolvingModel(source.autoMerging().conflictResolution().conflictResolvingModelAsString())
                            .sourceName(source.autoMerging().conflictResolution().sourceName())
                            .build())
                    .consolidation(source.autoMerging().consolidation() == null ? null :
                        software.amazon.customerprofiles.domain.Consolidation.builder()
                            .matchingAttributesList(source.autoMerging().consolidation().matchingAttributesList())
                            .build())
                    .minAllowedConfidenceScoreForMerging(source.autoMerging().minAllowedConfidenceScoreForMerging())
                    .build())
            .exportingConfig(source.exportingConfig() == null ? null :
                software.amazon.customerprofiles.domain.ExportingConfig.builder()
                    .s3Exporting(source.exportingConfig().s3Exporting() == null ? null :
                        software.amazon.customerprofiles.domain.S3ExportingConfig.builder()
                            .s3BucketName(source.exportingConfig().s3Exporting().s3BucketName())
                            .s3KeyName(source.exportingConfig().s3Exporting().s3KeyName())
                            .build())
                    .build())
            .jobSchedule(source.jobSchedule() == null ? null :
                software.amazon.customerprofiles.domain.JobSchedule.builder()
                    .dayOfTheWeek(source.jobSchedule().dayOfTheWeekAsString())
                    .time(source.jobSchedule().time())
                    .build())
            .build();
    }

    public static software.amazon.customerprofiles.domain.RuleBasedMatching translateToInternalRuleBasedMatchingResponse(
        RuleBasedMatchingResponse source) {
        if (source == null) {
            return null;
        }

        return software.amazon.customerprofiles.domain.RuleBasedMatching.builder()
            .enabled(source.enabled())
            .attributeTypesSelector(source.attributeTypesSelector() == null ? null :
                software.amazon.customerprofiles.domain.AttributeTypesSelector.builder()
                    .attributeMatchingModel(source.attributeTypesSelector().attributeMatchingModelAsString())
                    .address(source.attributeTypesSelector().address())
                    .emailAddress(source.attributeTypesSelector().emailAddress())
                    .phoneNumber(source.attributeTypesSelector().phoneNumber())
                    .build())
            .conflictResolution(source.conflictResolution() == null ? null :
                software.amazon.customerprofiles.domain.ConflictResolution.builder()
                    .conflictResolvingModel(source.conflictResolution().conflictResolvingModelAsString())
                    .sourceName(source.conflictResolution().sourceName())
                    .build())
            .exportingConfig(source.exportingConfig() == null ? null :
                software.amazon.customerprofiles.domain.ExportingConfig.builder()
                    .s3Exporting(source.exportingConfig().s3Exporting() == null ? null :
                        software.amazon.customerprofiles.domain.S3ExportingConfig.builder()
                            .s3BucketName(source.exportingConfig().s3Exporting().s3BucketName())
                            .s3KeyName(source.exportingConfig().s3Exporting().s3KeyName())
                            .build())
                    .build())
            .matchingRules((source.matchingRules() == null || source.matchingRules().size() == 0) ? null :
                source.matchingRules().stream()
                    .map(matchingRule -> software.amazon.customerprofiles.domain.MatchingRule.builder()
                        .rule(matchingRule.rule())
                        .build())
                    .collect(Collectors.toList()))
            .maxAllowedRuleLevelForMatching(source.maxAllowedRuleLevelForMatching())
            .maxAllowedRuleLevelForMerging(source.maxAllowedRuleLevelForMerging())
            .status(source.statusAsString())
            .build();
    }

    private static List<MatchingRule> toServiceMatchingRules(List<software.amazon.customerprofiles.domain.MatchingRule> matchingRules) {
        if (matchingRules == null || matchingRules.size() == 0) {
            return null;
        }
        return CollectionUtils.isNullOrEmpty(matchingRules)
            ? Collections.emptyList()
            : matchingRules.stream().map(t -> toServiceMatchingRule(t)).collect(Collectors.toList());
    }

    private static MatchingRule toServiceMatchingRule(software.amazon.customerprofiles.domain.MatchingRule matchingRule) {
        return MatchingRule.builder().rule(matchingRule.getRule()).build();
    }

    private static AttributeTypesSelector toServiceAttributeTypesSelector(
        software.amazon.customerprofiles.domain.AttributeTypesSelector attributeTypesSelector) {
        if (attributeTypesSelector == null) {
            return null;
        }
        return AttributeTypesSelector.builder()
            .attributeMatchingModel(attributeTypesSelector.getAttributeMatchingModel())
            .address(attributeTypesSelector.getAddress())
            .emailAddress(attributeTypesSelector.getEmailAddress())
            .phoneNumber(attributeTypesSelector.getPhoneNumber())
            .build();
    }

    private static JobSchedule toServiceJobSchedule(software.amazon.customerprofiles.domain.JobSchedule jobSchedule) {
        if (jobSchedule == null) {
            return null;
        }
        return JobSchedule.builder()
            .dayOfTheWeek(jobSchedule.getDayOfTheWeek())
            .time(jobSchedule.getTime())
            .build();
    }

    private static ExportingConfig toServiceExportingConfig(software.amazon.customerprofiles.domain.ExportingConfig exportingConfig) {
        if (exportingConfig == null) {
            return null;
        }
        return ExportingConfig.builder()
            .s3Exporting(exportingConfig.getS3Exporting() == null ? null :
                S3ExportingConfig.builder()
                    .s3BucketName(exportingConfig.getS3Exporting().getS3BucketName())
                    .s3KeyName(exportingConfig.getS3Exporting().getS3KeyName())
                    .build())
            .build();
    }

    private static AutoMerging toServiceAutoMerging(software.amazon.customerprofiles.domain.AutoMerging autoMerging) {
        if (autoMerging == null) {
            return null;
        }
        return AutoMerging.builder()
            .enabled(autoMerging.getEnabled())
            .conflictResolution(autoMerging.getConflictResolution() == null ? null :
                ConflictResolution.builder()
                    .conflictResolvingModel(autoMerging.getConflictResolution().getConflictResolvingModel())
                    .sourceName(autoMerging.getConflictResolution().getSourceName())
                    .build())
            .consolidation(autoMerging.getConsolidation() == null ? null :
                Consolidation.builder()
                    .matchingAttributesList(autoMerging.getConsolidation().getMatchingAttributesList())
                    .build())
            .minAllowedConfidenceScoreForMerging(autoMerging.getMinAllowedConfidenceScoreForMerging())
            .build();
    }

    private static ConflictResolution toServiceConflictResolution(software.amazon.customerprofiles.domain.ConflictResolution conflictResolution) {
        if (conflictResolution == null) {
            return null;
        }
        return ConflictResolution.builder()
            .conflictResolvingModel(conflictResolution.getConflictResolvingModel())
            .sourceName(conflictResolution.getSourceName())
            .build();
    }
}
