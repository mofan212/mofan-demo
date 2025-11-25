package indi.mofan.lombok.delegate;


import lombok.experimental.Delegate;

/**
 * @author mofan
 * @date 2025/11/25 15:06
 */
public class DelegationExample implements MyDelegate {
    @Delegate
    private final Delegation delegation = new Delegation();
}
