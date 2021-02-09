package software.amazon.customerprofiles.integration;

import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.DeleteIntegrationRequest;
import software.amazon.awssdk.services.customerprofiles.model.DeleteIntegrationResponse;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

@NoArgsConstructor
public class DeleteHandler extends BaseHandler<CallbackContext> {

    private CustomerProfilesClient client;

    public DeleteHandler(CustomerProfilesClient client) {
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

        final DeleteIntegrationRequest deleteIntegrationRequest = DeleteIntegrationRequest.builder()
                .domainName(model.getDomainName())
                .uri(model.getUri())
                .build();

        final DeleteIntegrationResponse deleteIntegrationResponse;
        try {
            deleteIntegrationResponse = proxy.injectCredentialsAndInvokeV2(deleteIntegrationRequest, client::deleteIntegration);
            logger.log(String.format("Integration deleted with domainName = %s, uri = %s",
                    model.getDomainName(), model.getUri()));
        } catch (BadRequestException e) {
            throw new CfnInvalidRequestException(e);
        } catch (InternalServerException e) {
            throw new CfnServiceInternalErrorException(e);
        } catch (ResourceNotFoundException e) {
            throw new CfnNotFoundException(e);
        } catch (Exception e) {
            throw new CfnGeneralServiceException(e);
        }

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(null)
                .status(OperationStatus.SUCCESS)
                .message(deleteIntegrationResponse.message())
                .build();
    }
}
