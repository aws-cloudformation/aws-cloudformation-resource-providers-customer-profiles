package software.amazon.customerprofiles.eventstream;

import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.DeleteEventStreamRequest;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
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

        final ResourceModel requestModel = request.getDesiredResourceState();

        final DeleteEventStreamRequest deleteEventStreamRequest = DeleteEventStreamRequest.builder()
            .domainName(requestModel.getDomainName())
            .eventStreamName(requestModel.getEventStreamName())
            .build();

        try {
            proxy.injectCredentialsAndInvokeV2(deleteEventStreamRequest, client::deleteEventStream);
            logger.log(String.format("Deleted event stream with name = %s from domain = %s",
                requestModel.getEventStreamName(), requestModel.getDomainName()));
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
            .build();
    }
}
