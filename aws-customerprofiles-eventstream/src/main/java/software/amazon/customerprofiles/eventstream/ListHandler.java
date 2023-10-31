package software.amazon.customerprofiles.eventstream;

import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.ListEventStreamsRequest;
import software.amazon.awssdk.services.customerprofiles.model.ListEventStreamsResponse;
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

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class ListHandler extends BaseHandler<CallbackContext> {
    private CustomerProfilesClient client;

    public ListHandler(CustomerProfilesClient client) {
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

        final ListEventStreamsRequest listEventStreamsRequest = ListEventStreamsRequest.builder()
            .domainName(requestModel.getDomainName())
            .nextToken(request.getNextToken())
            .build();
        final ListEventStreamsResponse listEventStreamsResponse;

        try {
            listEventStreamsResponse = proxy.injectCredentialsAndInvokeV2(listEventStreamsRequest, client::listEventStreams);
            logger.log(String.format("Listed event streams with domainName = %s", requestModel.getDomainName()));
        } catch (BadRequestException e) {
            throw new CfnInvalidRequestException(e);
        } catch (InternalServerException e) {
            throw new CfnServiceInternalErrorException(e);
        } catch (ResourceNotFoundException e) {
            throw new CfnNotFoundException(e);
        } catch (Exception e) {
            throw new CfnGeneralServiceException(e);
        }

        List<ResourceModel> responseModels = new ArrayList<>();
        listEventStreamsResponse.items().forEach(item -> {
            ResourceModel responseModel = ResourceModel.builder()
                .domainName(requestModel.getDomainName())
                .eventStreamName(item.eventStreamName())
                .uri(requestModel.getUri())
                .eventStreamArn(item.eventStreamArn())
                .state(item.state().toString())
                .tags(Translator.mapTagsToSet(item.tags()))
                .build();
            responseModels.add(responseModel);
        });

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModels(responseModels)
            .status(OperationStatus.SUCCESS)
            .nextToken(listEventStreamsResponse.nextToken())
            .build();
    }
}