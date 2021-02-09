package software.amazon.customerprofiles.domain;

import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.GetDomainRequest;
import software.amazon.awssdk.services.customerprofiles.model.GetDomainResponse;
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

        final GetDomainRequest getDomainRequest = GetDomainRequest.builder()
                .domainName(model.getDomainName())
                .build();

        final GetDomainResponse getDomainResponse;
        try {
            getDomainResponse = proxy.injectCredentialsAndInvokeV2(getDomainRequest, client::getDomain);
            logger.log(String.format("Get Domain with domainName = %s", model.getDomainName()));
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
                .createdAt(getDomainResponse.createdAt().toString())
                .deadLetterQueueUrl(getDomainResponse.deadLetterQueueUrl())
                .defaultEncryptionKey(getDomainResponse.defaultEncryptionKey())
                .defaultExpirationDays(getDomainResponse.defaultExpirationDays())
                .domainName(getDomainResponse.domainName())
                .lastUpdatedAt(getDomainResponse.lastUpdatedAt().toString())
                .tags(Translator.mapTagsToList(getDomainResponse.tags()))
                .build();

        return ProgressEvent.defaultSuccessHandler(responseModel);
    }
}
