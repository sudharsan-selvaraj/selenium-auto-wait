package io.github.sudharsan_selvaraj.autowait;

import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Builder
@Getter
public class SeleniumWaitOptions {

    @Builder.Default
    private final boolean ignoreWait = false;

    @Builder.Default
    private final Duration defaultWaitTime = Duration.ofSeconds(10);

    @Builder.Default
    private final List<String> excludedMethods = Collections.emptyList();

    @Builder.Default
    private final boolean parseAnnotations = false;

    private final String packageToBeParsed;

}
