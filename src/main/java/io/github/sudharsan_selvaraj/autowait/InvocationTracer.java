package io.github.sudharsan_selvaraj.autowait;

import lombok.AllArgsConstructor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import java.util.Objects;
import java.util.stream.Collectors;

@AllArgsConstructor
public class InvocationTracer {

    private final String packageTobeParsed;

    public List<Method> getTargetMethod() {
        Throwable t = new Throwable();
        List<StackTraceElement> stackTrace = Arrays.asList(t.getStackTrace());
        return stackTrace
                .stream()
                .filter(this::filterStackTrace)
                .map(this::getMethodFromStackTrace)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Method getMethodFromStackTrace(StackTraceElement stackTraceElement) {
        try {
            Class<?> callerClass = Class.forName(stackTraceElement.getClassName());
            return Arrays
                    .stream(callerClass.getDeclaredMethods())
                    .filter(m -> m.getName().equals(stackTraceElement.getMethodName()))
                    .collect(Collectors.toList())
                    .get(0);
        } catch (Throwable e) {
            return null;
        }
    }

    public Boolean filterStackTrace(StackTraceElement stackTrace) {
        if (packageTobeParsed != null) {
            return stackTrace.getClassName().startsWith(packageTobeParsed);
        } else {
            return stackTrace.getModuleName() == null || !stackTrace.getModuleName().startsWith("java.base");
        }
    }
}
