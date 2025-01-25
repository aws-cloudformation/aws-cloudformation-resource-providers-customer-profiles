package software.amazon.customerprofiles.eventtrigger;

import java.util.ArrayList;
import java.util.List;

import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.ListEventTriggersRequest;
import software.amazon.awssdk.services.customerprofiles.model.ListEventTriggersResponse;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ListHandler extends BaseHandlerStd {
    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final ProxyClient<CustomerProfilesClient> proxyClient,
            final Logger logger) {
        final ResourceModel requestModel = request.getDesiredResourceState();

        final ListEventTriggersRequest listEventTriggersRequest = ListEventTriggersRequest.builder()
                .domainName(requestModel.getDomainName())
                .nextToken(request.getNextToken())
                .build();
        final ListEventTriggersResponse listEventTriggersResponse;

        try {
            listEventTriggersResponse = proxy.injectCredentialsAndInvokeV2(listEventTriggersRequest, proxyClient.client()::listEventTriggers);
            logger.log(String.format("Listed event triggers with domainName = %s", requestModel.getDomainName()));
        } catch (BadRequestException exc) {
            throw new CfnInvalidRequestException(exc);
        } catch (InternalServerException exc) {
            throw new CfnServiceInternalErrorException(exc);
        } catch (ResourceNotFoundException exc) {
            throw new CfnNotFoundException(exc);
        } catch (Exception exc) {
            throw new CfnGeneralServiceException(exc);
        }

        List<ResourceModel> responseModels = new ArrayList<>();
        listEventTriggersResponse.items().forEach(item -> {
            ResourceModel responseModel = ResourceModel.builder()
                    .domainName(requestModel.getDomainName())
                    .eventTriggerName(item.eventTriggerName())
                    .objectTypeName(item.objectTypeName())
                    .description(item.description())
                    .createdAt(item.createdAt() == null ? null : item.createdAt().toString())
                    .lastUpdatedAt(item.lastUpdatedAt() == null ? null : item.lastUpdatedAt().toString())
                    .tags(Translator.mapTagsToSet(item.tags()))
                    .build();
            responseModels.add(responseModel);
        });

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModels(responseModels)
                .status(OperationStatus.SUCCESS)
                .nextToken(listEventTriggersResponse.nextToken())
                .build();
    }
}
