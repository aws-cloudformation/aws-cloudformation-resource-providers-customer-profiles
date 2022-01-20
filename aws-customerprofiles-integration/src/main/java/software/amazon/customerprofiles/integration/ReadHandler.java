package software.amazon.customerprofiles.integration;

import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.GetIntegrationRequest;
import software.amazon.awssdk.services.customerprofiles.model.GetIntegrationResponse;
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

        final ResourceModel requestModel = request.getDesiredResourceState();

        final GetIntegrationRequest getIntegrationRequest = GetIntegrationRequest.builder()
                .domainName(requestModel.getDomainName())
                .uri(requestModel.getUri())
                .build();

        final GetIntegrationResponse getIntegrationResponse;
        try {
            getIntegrationResponse = proxy.injectCredentialsAndInvokeV2(getIntegrationRequest, client::getIntegration);
            logger.log(String.format("Get Integration with domainName = %s, uri = %s",
                    requestModel.getDomainName(), requestModel.getUri()));
        } catch (BadRequestException e) {
            throw new CfnInvalidRequestException(e);
        } catch (InternalServerException e) {
            throw new CfnServiceInternalErrorException(e);
        } catch (ResourceNotFoundException e) {
            throw new CfnNotFoundException(e);
        } catch (Exception e) {
            throw new CfnGeneralServiceException(e);
        }

        final ResourceModel responseModel = ResourceModel.builder()
                .createdAt(getIntegrationResponse.createdAt().toString())
                .domainName(getIntegrationResponse.domainName())
                .lastUpdatedAt(getIntegrationResponse.lastUpdatedAt().toString())
                .objectTypeName(getIntegrationResponse.objectTypeName())
                .tags(Translator.mapTagsToList(getIntegrationResponse.tags()))
                .uri(getIntegrationResponse.uri())
                .objectTypeNames(Translator.mapObjectTypeNamesToList(getIntegrationResponse.objectTypeNames()))
                .build();

        return ProgressEvent.defaultSuccessHandler(responseModel);
    }
}
