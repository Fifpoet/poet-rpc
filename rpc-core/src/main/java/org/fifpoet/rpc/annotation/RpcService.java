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
    String version() default "0";

    /**
     * 1. when one impl only: registry name -> interface+version
     * 2. when interface implemented by more than one impl, designate the impl: registry name -> interface+@impl+version
     * @return the impl identity
     */
    String impl() default "";
}
