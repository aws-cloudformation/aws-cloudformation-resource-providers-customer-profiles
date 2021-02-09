package software.amazon.customerprofiles.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import software.amazon.cloudformation.proxy.StdCallbackContext;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class CallbackContext extends StdCallbackContext {
}
