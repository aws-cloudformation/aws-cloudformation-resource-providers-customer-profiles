package software.amazon.customerprofiles.domain;

import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.ListDomainsRequest;
import software.amazon.awssdk.services.customerprofiles.model.ListDomainsResponse;
import software.amazon.awssdk.services.customerprofiles.model.ListIntegrationsResponse;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
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

        final ListDomainsRequest listDomainsRequest = ListDomainsRequest.builder()
                .nextToken(request.getNextToken())
                .build();

        final ListDomainsResponse listDomainsResponse;
        try {
            listDomainsResponse = proxy.injectCredentialsAndInvokeV2(listDomainsRequest, client::listDomains);
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
        listDomainsResponse.items().forEach(res -> {
            ResourceModel responseModel = ResourceModel.builder()
                    .createdAt(res.createdAt().toString())
                    .domainName(res.domainName())
                    .lastUpdatedAt(res.lastUpdatedAt().toString())
                    .tags(Translator.mapTagsToList(res.tags()))
                    .build();
            responseModels.add(responseModel);
        });

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModels(responseModels)
                .status(OperationStatus.SUCCESS)
                .nextToken(listDomainsResponse.nextToken())
                .build();
    }
}
