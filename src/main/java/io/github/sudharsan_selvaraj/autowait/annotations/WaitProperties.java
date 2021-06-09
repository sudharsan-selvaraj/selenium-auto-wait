package io.github.sudharsan_selvaraj.autowait.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WaitProperties {

    /**
     * In seconds
     */
    int timeout() default 0;

    String[] exclude() default {};

}
