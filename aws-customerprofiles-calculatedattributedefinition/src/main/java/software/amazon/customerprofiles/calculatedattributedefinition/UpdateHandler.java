package software.amazon.customerprofiles.calculatedattributedefinition;

import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.GetCalculatedAttributeDefinitionRequest;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.ResourceNotFoundException;
import software.amazon.awssdk.services.customerprofiles.model.TagResourceRequest;
import software.amazon.awssdk.services.customerprofiles.model.UntagResourceRequest;
import software.amazon.awssdk.services.customerprofiles.model.UpdateCalculatedAttributeDefinitionRequest;
import software.amazon.awssdk.services.customerprofiles.model.UpdateCalculatedAttributeDefinitionResponse;
import software.amazon.cloudformation.exceptions.CfnGeneralServiceException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.exceptions.CfnServiceInternalErrorException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
public class UpdateHandler extends BaseHandler<CallbackContext> {
    private CustomerProfilesClient client;

    public UpdateHandler(CustomerProfilesClient client) {
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

        // check whether the definition exists
        try {
            proxy.injectCredentialsAndInvokeV2(getDefinitionRequest, client::getCalculatedAttributeDefinition);
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

        final Set<Tag> previousTags = request.getPreviousResourceTags() == null ?
                new HashSet<>() : Translator.mapTagsToSet(request.getPreviousResourceTags());

        if (previousTags != null) {
            final List<String> tagsToRemove = previousTags.stream()
                    .map(Tag::getKey)
                    .collect(Collectors.toList());

            // remove previous tags
            if (tagsToRemove.size() > 0) {
                final UntagResourceRequest untagResourceRequest = UntagResourceRequest.builder()
                        .resourceArn(Translator.toCalculatedAttributeDefinitionArn(request))
                        .tagKeys(tagsToRemove)
                        .build();
                proxy.injectCredentialsAndInvokeV2(untagResourceRequest, client::untagResource);
            }
        }
        if (request.getDesiredResourceTags() != null && !request.getDesiredResourceTags().isEmpty()) {
            final Map<String, String> resourceTags = request.getDesiredResourceTags();
            final TagResourceRequest tagResourceRequest = TagResourceRequest.builder()
                    .resourceArn(Translator.toCalculatedAttributeDefinitionArn(request))
                    .tags(resourceTags)
                    .build();
            proxy.injectCredentialsAndInvokeV2(tagResourceRequest, client::tagResource);
        }

        final UpdateCalculatedAttributeDefinitionRequest updateDefinitionRequest = UpdateCalculatedAttributeDefinitionRequest.builder()
                .domainName(requestModel.getDomainName())
                .calculatedAttributeName(requestModel.getCalculatedAttributeName())
                .displayName(requestModel.getDisplayName())
                .description(requestModel.getDescription())
                .conditions(Translator.translateFromInternalConditions(requestModel.getConditions()))
                .build();
        final UpdateCalculatedAttributeDefinitionResponse updateDefinitionResponse;

        try {
            updateDefinitionResponse = proxy.injectCredentialsAndInvokeV2(updateDefinitionRequest, client::updateCalculatedAttributeDefinition);
            logger.log(String.format("Updated calculated attribute definition with domainName = %s, calculatedAttributeName = %s",
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
                .calculatedAttributeName(updateDefinitionResponse.calculatedAttributeName())
                .displayName(updateDefinitionResponse.displayName())
                .description(updateDefinitionResponse.description())
                .createdAt(updateDefinitionResponse.createdAt().toString())
                .lastUpdatedAt(updateDefinitionResponse.lastUpdatedAt().toString())
                .statistic(updateDefinitionResponse.statisticAsString())
                .attributeDetails(Translator.translateToInternalAttributeDetails(updateDefinitionResponse.attributeDetails()))
                .conditions(Translator.translateToInternalConditions(updateDefinitionResponse.conditions()))
                .tags(Translator.mapTagsToSet(updateDefinitionResponse.tags()))
                .build();

        return ProgressEvent.defaultSuccessHandler(responseModel);
    }
}
