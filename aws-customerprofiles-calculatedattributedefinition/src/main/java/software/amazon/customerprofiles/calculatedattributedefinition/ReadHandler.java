package software.amazon.customerprofiles.calculatedattributedefinition;

import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.GetCalculatedAttributeDefinitionRequest;
import software.amazon.awssdk.services.customerprofiles.model.GetCalculatedAttributeDefinitionResponse;
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

        final GetCalculatedAttributeDefinitionRequest getDefinitionRequest = GetCalculatedAttributeDefinitionRequest.builder()
                .domainName(requestModel.getDomainName())
                .calculatedAttributeName(requestModel.getCalculatedAttributeName())
                .build();
        final GetCalculatedAttributeDefinitionResponse getDefinitionResponse;

        try {
            getDefinitionResponse = proxy.injectCredentialsAndInvokeV2(getDefinitionRequest, client::getCalculatedAttributeDefinition);
            logger.log(String.format("Got calculated attribute definition with domainName = %s, calculatedAttributeName = %s",
                    requestModel.getDomainName(), requestModel.getCalculatedAttributeName()));
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
                .domainName(requestModel.getDomainName())
                .calculatedAttributeName(getDefinitionResponse.calculatedAttributeName())
                .displayName(getDefinitionResponse.displayName())
                .description(getDefinitionResponse.description())
                .createdAt(getDefinitionResponse.createdAt() == null ? null : getDefinitionResponse.createdAt().toString())
                .lastUpdatedAt(getDefinitionResponse.lastUpdatedAt() == null ? null : getDefinitionResponse.lastUpdatedAt().toString())
                .statistic(getDefinitionResponse.statisticAsString())
                .attributeDetails(Translator.translateToInternalAttributeDetails(getDefinitionResponse.attributeDetails()))
                .conditions(Translator.translateToInternalConditions(getDefinitionResponse.conditions()))
                .tags(Translator.mapTagsToSet(getDefinitionResponse.tags()))
                .build();

        return ProgressEvent.defaultSuccessHandler(responseModel);
    }
}
