package ru.forum.whale.space.api.aspect;

import ru.forum.whale.space.api.model.LogType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableActionLogging {
    LogType logType();
}
