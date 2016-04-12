package org.openbaton.nfvo.vnfm_reg.tasks;

/**
 * Created by lto on 12/04/16.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

public class TaskExceptionHandler implements AsyncUncaughtExceptionHandler {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.error("Method Name::"+method.getName());
        log.error("Exception occurred::"+ ex);
        ex.printStackTrace();
    }
}
