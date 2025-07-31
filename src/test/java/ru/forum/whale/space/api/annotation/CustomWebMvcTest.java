package ru.forum.whale.space.api.annotation;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.ActiveProfiles;
import ru.forum.whale.space.api.config.SecurityConfig;
import ru.forum.whale.space.api.handler.AccessDeniedExceptionHandler;
import ru.forum.whale.space.api.handler.AuthExceptionHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ActiveProfiles("test")
@Import({SecurityConfig.class, AuthExceptionHandler.class, AccessDeniedExceptionHandler.class})
@WebMvcTest
public @interface CustomWebMvcTest {
    @AliasFor(annotation = WebMvcTest.class, attribute = "controllers")
    Class<?>[] value() default {};

    @AliasFor(annotation = WebMvcTest.class, attribute = "controllers")
    Class<?>[] controllers() default {};
}
