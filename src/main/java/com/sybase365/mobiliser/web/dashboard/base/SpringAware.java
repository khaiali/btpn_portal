package com.sybase365.mobiliser.web.dashboard.base;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author all
 */
public final class SpringAware implements ApplicationContextAware {

    @Override
    public void setApplicationContext(final ApplicationContext appContext) {
        SpringAdapter.setContext(appContext);
    }
}
