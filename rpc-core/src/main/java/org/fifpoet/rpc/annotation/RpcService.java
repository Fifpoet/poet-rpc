package org.fifpoet.rpc.annotation;

import java.lang.annotation.*;

/**
 * apply on Rpc ServiceImpl, to trigger service registration
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface RpcService {
    String group() default "DFT";
}
