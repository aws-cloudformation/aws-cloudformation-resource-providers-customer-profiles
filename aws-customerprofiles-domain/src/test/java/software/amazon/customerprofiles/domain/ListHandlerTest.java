package software.amazon.customerprofiles.domain;

import com.google.common.collect.Lists;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.customerprofiles.CustomerProfilesClient;
import software.amazon.awssdk.services.customerprofiles.model.ListDomainItem;
import software.amazon.awssdk.services.customerprofiles.model.ListDomainsResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ListHandlerTest {
    private static final Instant TIME = Instant.now();
    private static final String DOMAIN_NAME_1 = "testDomainName1";
    private static final String DOMAIN_NAME_2 = "testDomainName2";

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private CustomerProfilesClient customerProfilesClient;

    @Mock
    private Logger logger;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void handleRequest_SimpleSuccess() {
        final ListHandler handler = new ListHandler(customerProfilesClient);

        final ResourceModel model = ResourceModel.builder().build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();

        ListDomainItem listDomainItem1 = ListDomainItem.builder()
                .domainName(DOMAIN_NAME_1)
                .createdAt(TIME)
                .lastUpdatedAt(TIME)
                .build();

        ListDomainItem listDomainItem2 = ListDomainItem.builder()
                .domainName(DOMAIN_NAME_2)
                .createdAt(TIME)
                .lastUpdatedAt(TIME)
                .build();

        final ListDomainsResponse listDomainsResponse = ListDomainsResponse.builder()
                .items(Lists.newArrayList(listDomainItem1, listDomainItem2))
                .build();

        Mockito.when(proxy.injectCredentialsAndInvokeV2(any(), any()))
                .thenReturn(listDomainsResponse);

        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels().get(0).getDomainName()).isEqualTo(DOMAIN_NAME_1);
        assertThat(response.getResourceModels().get(1).getDomainName()).isEqualTo(DOMAIN_NAME_2);
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }
}
