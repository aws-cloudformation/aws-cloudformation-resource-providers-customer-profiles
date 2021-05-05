package software.amazon.customerprofiles.integration.translators;

import com.amazonaws.util.StringUtils;
import software.amazon.awssdk.services.customerprofiles.model.ConnectorOperator;
import software.amazon.awssdk.services.customerprofiles.model.S3SourceProperties;
import software.amazon.awssdk.services.customerprofiles.model.SourceConnectorProperties;
import software.amazon.awssdk.services.customerprofiles.model.SourceFlowConfig;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;

public class S3Translator implements ConnectorTranslator {

    @Override
    public SourceFlowConfig toServiceSourceFlowConfig(software.amazon.customerprofiles.integration.SourceFlowConfig model) {
        return SourceFlowConfig.builder()
                .connectorProfileName(StringUtils.isNullOrEmpty(model.getConnectorProfileName()) ? null :
                        model.getConnectorProfileName())
                .connectorType(model.getConnectorType())
                .sourceConnectorProperties(
                        SourceConnectorProperties.builder()
                                .s3(toServiceS3SourceProperties(model
                                        .getSourceConnectorProperties()
                                        .getS3()))
                                .build()
                )
                .build();
    }

    private S3SourceProperties toServiceS3SourceProperties(software.amazon.customerprofiles.integration.S3SourceProperties model) {
        if (model == null) {
            throw new CfnInvalidRequestException("SourceConnectorType and SourceConnectorProperties must be same type.");
        }
        return S3SourceProperties.builder()
                .bucketPrefix(model.getBucketPrefix())
                .bucketName(model.getBucketName())
                .build();
    }

    @Override
    public ConnectorOperator toServiceConnectorOperator(software.amazon.customerprofiles.integration.ConnectorOperator model) {
        if (model == null) {
            return null;
        }
        return ConnectorOperator.builder()
                .s3(model.getS3())
                .build();
    }
}
