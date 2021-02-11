package software.amazon.customerprofiles.objecttype;

import com.google.common.collect.Lists;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.GetProfileObjectTypeRequest;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.PutProfileObjectTypeRequest;
import software.amazon.awssdk.services.customerprofiles.model.PutProfileObjectTypeResponse;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.awssdk.services.customerprofiles.model.UntagResourceRequest;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
public class UpdateHandler extends BaseHandler<CallbackContext> {

    private CustomerProfilesClient client;

    public UpdateHandler(CustomerProfilesClient client) {
        this.client = client;
    }

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {

        if (this.client == null) {
            this.client = ClientBuilder.getClient();
        }

        final ResourceModel model = request.getDesiredResourceState();

        final GetProfileObjectTypeRequest getProfileObjectTypeRequest = GetProfileObjectTypeRequest.builder()
                .domainName(model.getDomainName())
                .objectTypeName(model.getObjectTypeName())
                .build();

        // If this objectType is never created, can not be updated
        try {
            proxy.injectCredentialsAndInvokeV2(getProfileObjectTypeRequest, client::getProfileObjectType);
            logger.log(String.format("Get Domain with domainName = %s",
                    model.getDomainName()));
        } catch (BadRequestException e) {
            throw new CfnInvalidRequestException(e);
        } catch (InternalServerException e) {
            throw new CfnServiceInternalErrorException(e);
        } catch (ResourceNotFoundException e) {
            throw new CfnNotFoundException(e);
        } catch (Exception e) {
            throw new CfnGeneralServiceException(e);
        }

        final List<Tag> previousTags = request.getPreviousResourceTags() == null ? Lists.newArrayList() :
                Translator.mapTagsToList(request.getPreviousResourceTags());

        if (previousTags != null) {
            final List<String> tagsToRemove = previousTags.stream()
                    .map(Tag::getKey)
                    .collect(Collectors.toList());

            // Remove previous tags
            if (tagsToRemove.size() > 0) {
                final UntagResourceRequest untagResourceRequest = UntagResourceRequest.builder()
                        .resourceArn(Translator.toProfileObjectTypeARN(request))
                        .tagKeys(tagsToRemove)
                        .build();
                proxy.injectCredentialsAndInvokeV2(untagResourceRequest, client::untagResource);
            }
        }

        final Map<String, String> resourceTag;
        if (request.getDesiredResourceTags() == null) {
            resourceTag = null;
        } else if (request.getDesiredResourceTags().isEmpty()) {
            resourceTag = null;
        } else {
            resourceTag = request.getDesiredResourceTags();
        }
        final PutProfileObjectTypeRequest putProfileObjectTypeRequest = PutProfileObjectTypeRequest.builder()
                .domainName(model.getDomainName())
                .objectTypeName(model.getObjectTypeName())
                .allowProfileCreation(model.getAllowProfileCreation())
                .description(model.getDescription())
                .encryptionKey(model.getEncryptionKey())
                .expirationDays(model.getExpirationDays())
                .fields(Translator.listFieldsToMap(model.getFields()))
                .keys(Translator.listKeysToMap(model.getKeys()))
                .tags(resourceTag)
                .templateId(model.getTemplateId())
                .build();

        final PutProfileObjectTypeResponse putProfileObjectTypeResponse;
        try {
            putProfileObjectTypeResponse = proxy.injectCredentialsAndInvokeV2(putProfileObjectTypeRequest, client::putProfileObjectType);
            logger.log(String.format("Update ProfileObjectType with domainName = %s, objectTypeName = %s",
                    model.getDomainName(), model.getObjectTypeName()));
        } catch (BadRequestException e) {
            throw new CfnInvalidRequestException(e);
        } catch (ResourceNotFoundException e) {
            throw new CfnNotFoundException(e);
        } catch (InternalServerException e) {
            throw new CfnServiceInternalErrorException(e);
        } catch (Exception e) {
            throw new CfnGeneralServiceException(e);
        }

        final ResourceModel responseModel = ResourceModel.builder()
                .domainName(model.getDomainName())
                .allowProfileCreation(putProfileObjectTypeResponse.allowProfileCreation())
                .createdAt(putProfileObjectTypeResponse.createdAt().toString())
                .description(putProfileObjectTypeResponse.description())
                .encryptionKey(putProfileObjectTypeResponse.encryptionKey())
                .expirationDays(putProfileObjectTypeResponse.expirationDays())
                .fields(Translator.mapFieldsToList(putProfileObjectTypeResponse.fields()))
                .keys(Translator.mapKeysToList(putProfileObjectTypeResponse.keys()))
                .lastUpdatedAt(putProfileObjectTypeResponse.lastUpdatedAt().toString())
                .objectTypeName(putProfileObjectTypeResponse.objectTypeName())
                .tags(Translator.mapTagsToList(putProfileObjectTypeResponse.tags()))
                .templateId(putProfileObjectTypeResponse.templateId())
                .build();

        return ProgressEvent.defaultSuccessHandler(responseModel);
    }
}
