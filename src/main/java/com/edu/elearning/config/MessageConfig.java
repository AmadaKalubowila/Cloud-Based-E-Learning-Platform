package com.edu.elearning.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Primary;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@Primary
@RequiredArgsConstructor
public class MessageConfig {
    private final MessageSource ms;

    public String get(String key, Object... args) {
        return ms.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    public String getOrDefault(String key, String defaultMsg, Object... args) {
        return ms.getMessage(key, args, defaultMsg, LocaleContextHolder.getLocale());
    }
}
