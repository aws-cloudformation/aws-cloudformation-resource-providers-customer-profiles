package software.amazon.customerprofiles.eventtrigger;

import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.GetEventTriggerRequest;
import software.amazon.awssdk.services.customerprofiles.model.GetEventTriggerResponse;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ReadHandler extends BaseHandlerStd {
    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<CustomerProfilesClient> proxyClient,
            final Logger logger) {
        final ResourceModel requestModel = request.getDesiredResourceState();

        final GetEventTriggerRequest getEventTriggerRequest = GetEventTriggerRequest.builder()
                .domainName(requestModel.getDomainName())
                .eventTriggerName(requestModel.getEventTriggerName())
                .build();
        final GetEventTriggerResponse getEventTriggerResponse;

        try {
            getEventTriggerResponse = proxy.injectCredentialsAndInvokeV2(getEventTriggerRequest, proxyClient.client()::getEventTrigger);
            logger.log(String.format("Got event trigger with domainName = %s, eventTriggerName = %s",
                    requestModel.getDomainName(), requestModel.getEventTriggerName()));
        } catch (BadRequestException exc) {
            throw new CfnInvalidRequestException(exc);
        } catch (InternalServerException exc) {
            throw new CfnServiceInternalErrorException(exc);
        } catch (ResourceNotFoundException exc) {
            throw new CfnNotFoundException(exc);
        } catch (Exception exc) {
            throw new CfnGeneralServiceException(exc);
        }

        final ResourceModel responseModel = ResourceModel.builder()
                .domainName(requestModel.getDomainName())
                .eventTriggerName(getEventTriggerResponse.eventTriggerName())
                .objectTypeName(getEventTriggerResponse.objectTypeName())
                .description(getEventTriggerResponse.description())
                .eventTriggerConditions(Translator.translateToInternalEventTriggerConditions(getEventTriggerResponse.eventTriggerConditions()))
                .segmentFilter(getEventTriggerResponse.segmentFilter())
                .eventTriggerLimits(Translator.translateToInternalEventTriggerLimits(getEventTriggerResponse.eventTriggerLimits()))
                .createdAt(getEventTriggerResponse.createdAt() == null ? null : getEventTriggerResponse.createdAt().toString())
                .lastUpdatedAt(getEventTriggerResponse.lastUpdatedAt() == null ? null : getEventTriggerResponse.lastUpdatedAt().toString())
                .tags(Translator.mapTagsToSet(getEventTriggerResponse.tags()))
                .build();

        return ProgressEvent.defaultSuccessHandler(responseModel);
    }
}
