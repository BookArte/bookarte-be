package com.library.bookarte.global.util;

import com.nhncorp.lucy.security.xss.XssFilter;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Component;

@Component
public class XssUtils {
    private final XssFilter xssFilter = XssFilter.getInstance("lucy-xss-superset.xml");

    public String filterEditor(String content) {
        if (content == null) return null;
        return xssFilter.doFilter(content);
    }

    public String escapeText(String text) {
        if (text == null) return null;
        return StringEscapeUtils.escapeHtml4(text);
    }
}
