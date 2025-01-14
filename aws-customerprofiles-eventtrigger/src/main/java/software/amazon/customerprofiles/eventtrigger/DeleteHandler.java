package software.amazon.customerprofiles.eventtrigger;

import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.DeleteEventTriggerRequest;
import software.amazon.awssdk.services.customerprofiles.model.DeleteEventTriggerResponse;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class DeleteHandler extends BaseHandlerStd {
    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<CustomerProfilesClient> proxyClient,
            final Logger logger) {
        final ResourceModel requestModel = request.getDesiredResourceState();

        final DeleteEventTriggerRequest deleteEventTriggerRequest = DeleteEventTriggerRequest.builder()
                .domainName(requestModel.getDomainName())
                .eventTriggerName(requestModel.getEventTriggerName())
                .build();
        final DeleteEventTriggerResponse deleteEventTriggerResponse;


        try {
            deleteEventTriggerResponse = proxy.injectCredentialsAndInvokeV2(deleteEventTriggerRequest, proxyClient.client()::deleteEventTrigger);
            logger.log(String.format("Deleted event trigger with domainName = %s, eventTriggerName = %s",
                    requestModel.getDomainName(), requestModel.getEventTriggerName()));
        } catch (BadRequestException exc) {
            throw new CfnInvalidRequestException(exc);
        } catch (InternalServerException exc) {
            throw new CfnServiceInternalErrorException(exc);
        } catch (ResourceNotFoundException exc) {
            throw new CfnNotFoundException(exc);
        } catch (Exception exc) {
            throw Translator.translateToCfnException(exc);
        }

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(null)
                .status(OperationStatus.SUCCESS)
                .message(deleteEventTriggerResponse.message())
                .build();
    }
}