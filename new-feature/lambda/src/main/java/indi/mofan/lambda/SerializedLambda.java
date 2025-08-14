package indi.mofan.lambda;

import java.io.Serializable;

/**
 * @author mofan
 * @date 2022/7/3 18:41
 */
@SuppressWarnings("ALL")
public class SerializedLambda implements Serializable {
    private static final long serialVersionUID = 8025925345765570181L;
    private  Class<?> capturingClass;
    private  String functionalInterfaceClass;
    private  String functionalInterfaceMethodName;
    private  String functionalInterfaceMethodSignature;
    private  String implClass;
    private  String implMethodName;
    private  String implMethodSignature;
    private  int implMethodKind;
    private  String instantiatedMethodType;
    private  Object[] capturedArgs;

    public String getCapturingClass() {
        return capturingClass.getName().replace('.', '/');
    }
    public String getFunctionalInterfaceClass() {
        return functionalInterfaceClass;
    }
    public String getFunctionalInterfaceMethodName() {
        return functionalInterfaceMethodName;
    }
    public String getFunctionalInterfaceMethodSignature() {
        return functionalInterfaceMethodSignature;
    }
    public String getImplClass() {
        return implClass;
    }
    public String getImplMethodName() {
        return implMethodName;
    }
    public String getImplMethodSignature() {
        return implMethodSignature;
    }
    public int getImplMethodKind() {
        return implMethodKind;
    }
    public final String getInstantiatedMethodType() {
        return instantiatedMethodType;
    }
    public int getCapturedArgCount() {
        return capturedArgs.length;
    }
    public Object getCapturedArg(int i) {
        return capturedArgs[i];
    }
}
