package com.jd.accounting.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Locale;

@Component
public class ResourceRepository {

    @Autowired
    private MessageSource messageSource;

    private MessageSourceAccessor accessor;

    @PostConstruct
    private void init() {
        accessor = new MessageSourceAccessor(messageSource); //, Locale.ENGLISH);
    }

    public String getResource(String key, String param1, String param2) {
        final String[] params = new String[]{param1, param2};
        final String msg = accessor.getMessage(key, params, LocaleContextHolder.getLocale());

        return msg;
    }

    public String getResource(String key, String param1) {
        return getResource(key, param1, null);
    }

    public String getResource(String key) {
        return getResource(key, null, null);
    }

}
