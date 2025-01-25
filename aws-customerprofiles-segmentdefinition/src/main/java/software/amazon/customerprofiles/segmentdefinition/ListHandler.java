package software.amazon.customerprofiles.segmentdefinition;

import java.util.ArrayList;
import java.util.List;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.BadRequestException;
import software.amazon.awssdk.services.customerprofiles.model.InternalServerException;
import software.amazon.awssdk.services.customerprofiles.model.ListSegmentDefinitionsRequest;
import software.amazon.awssdk.services.customerprofiles.model.ListSegmentDefinitionsResponse;
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

public class ListHandler extends ProfileBaseHandler {

  public ListHandler() {
    super();
  }

  protected ListHandler(CustomerProfilesClient client) {
    super(client);
  }

  @Override
  public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
      final AmazonWebServicesClientProxy proxy,
      final ResourceHandlerRequest<ResourceModel> request,
      final CallbackContext callbackContext,
      final ProxyClient<CustomerProfilesClient> proxyClient,
      final Logger logger) {

    final ResourceModel requestModel = request.getDesiredResourceState();

    final ListSegmentDefinitionsRequest listDefinitionsRequest = ListSegmentDefinitionsRequest.builder()
        .domainName(requestModel.getDomainName())
        .nextToken(request.getNextToken())
        .build();
    final ListSegmentDefinitionsResponse listDefinitionsResponse;

    try {
      listDefinitionsResponse = proxy.injectCredentialsAndInvokeV2(listDefinitionsRequest,
          client::listSegmentDefinitions);
      logger.log(String.format("Listed segment definitions with domainName = %s",
          requestModel.getDomainName()));
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
    listDefinitionsResponse.items().forEach(item -> {
      ResourceModel responseModel = ResourceModel.builder()
          .domainName(requestModel.getDomainName())
          .segmentDefinitionName(item.segmentDefinitionName())
          .displayName(item.displayName())
          .description(item.description())
          .segmentDefinitionArn(item.segmentDefinitionArn())
          .createdAt(item.createdAt() == null ? null : item.createdAt().toString())
          .tags(Translator.mapTagsToSet(item.tags()))
          .build();
      responseModels.add(responseModel);
    });

    return ProgressEvent.<ResourceModel, CallbackContext>builder()
        .resourceModels(responseModels)
        .status(OperationStatus.SUCCESS)
        .nextToken(listDefinitionsResponse.nextToken())
        .build();
  }
}
