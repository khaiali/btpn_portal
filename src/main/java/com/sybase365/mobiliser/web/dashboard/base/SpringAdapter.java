package com.sybase365.mobiliser.web.dashboard.base;

import org.springframework.context.ApplicationContext;

/**
 * Glue code for legacy classes not managed by Spring.
 * 
 * @author all
 */
public final class SpringAdapter {

    private static ApplicationContext context;

    public static ApplicationContext getContext() {
        return context;
    }

    public static void setContext(final ApplicationContext appContext) {
        context = appContext;
    }
}
