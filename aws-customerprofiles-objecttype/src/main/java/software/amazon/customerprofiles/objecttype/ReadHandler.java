package software.amazon.customerprofiles.objecttype;

import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.GetProfileObjectTypeRequest;
import software.amazon.awssdk.services.customerprofiles.model.GetProfileObjectTypeResponse;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

@NoArgsConstructor
public class ReadHandler extends BaseHandler<CallbackContext> {

    private CustomerProfilesClient client;

    public ReadHandler(CustomerProfilesClient client) {
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

        final GetProfileObjectTypeResponse getProfileObjectTypeResponse;
        try {
            getProfileObjectTypeResponse = proxy.injectCredentialsAndInvokeV2(getProfileObjectTypeRequest, client::getProfileObjectType);
            logger.log(String.format("Get ProfileObjectType with domainName = %s, objectTypeName = %s",
                    model.getDomainName(), model.getObjectTypeName()));
        } catch (BadRequestException e) {
            throw new CfnInvalidRequestException(e);
        } catch (InternalServerException e) {
            throw new CfnServiceInternalErrorException(e);
        } catch (ResourceNotFoundException e) {
            throw new CfnNotFoundException(e);
        } catch (Exception e) {
            throw new CfnGeneralServiceException(e);
        }

        final ResourceModel responseModel = getResourceModel(model, getProfileObjectTypeResponse);

        return ProgressEvent.defaultSuccessHandler(responseModel);
    }

    private ResourceModel getResourceModel(ResourceModel model, GetProfileObjectTypeResponse getProfileObjectTypeResponse) {
        ResourceModel responseModel;
        try {
            responseModel = ResourceModel.builder()
                .domainName(model.getDomainName())
                .allowProfileCreation(getProfileObjectTypeResponse.allowProfileCreation())
                .createdAt(getProfileObjectTypeResponse.createdAt() == null ? null : getProfileObjectTypeResponse.createdAt().toString())
                .description(getProfileObjectTypeResponse.description())
                .encryptionKey(getProfileObjectTypeResponse.encryptionKey())
                .expirationDays(getProfileObjectTypeResponse.expirationDays())
                .fields(Translator.mapFieldsToList(getProfileObjectTypeResponse.fields()))
                .keys(Translator.mapKeysToList(getProfileObjectTypeResponse.keys()))
                .lastUpdatedAt(getProfileObjectTypeResponse.lastUpdatedAt() == null ? null : getProfileObjectTypeResponse.lastUpdatedAt().toString())
                .objectTypeName(getProfileObjectTypeResponse.objectTypeName())
                .tags(Translator.mapTagsToList(getProfileObjectTypeResponse.tags()))
                .templateId(getProfileObjectTypeResponse.templateId())
                .sourceLastUpdatedTimestampFormat(getProfileObjectTypeResponse.sourceLastUpdatedTimestampFormat())
                .build();
        } catch (Exception e) {
            throw new CfnGeneralServiceException(e);
        }
        return responseModel;
    }
}
