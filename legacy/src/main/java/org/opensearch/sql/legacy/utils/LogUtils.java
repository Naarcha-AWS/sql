/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */


package org.opensearch.sql.legacy.utils;

import java.util.Map;
import java.util.UUID;
import org.apache.logging.log4j.ThreadContext;

/**
 * Utility class for generating/accessing the request id from logging context.
 */
public class LogUtils {

    /**
     * The key of the request id in the context map
     */
    private static final String REQUEST_ID_KEY = "request_id";

    /**
     * Generates a random UUID and adds to the {@link ThreadContext} as the request id.
     * <p>
     * Note: If a request id already present, this method will overwrite it with a new
     * one. This is to pre-vent re-using the same request id for different requests in
     * case the same thread handles both of them. But this also means one should not
     * call this method twice on the same thread within the lifetime of the request.
     * </p>
     */
    public static void addRequestId() {

        ThreadContext.put(REQUEST_ID_KEY, UUID.randomUUID().toString());
    }

    /**
     * @return the current request id from {@link ThreadContext}
     */
    public static String getRequestId() {

        final String requestId = ThreadContext.get(REQUEST_ID_KEY);
        if (requestId == null) {
            throw new IllegalStateException("Request id not present in current context");
        }

        return requestId;
    }

    /**
     * Wraps a given instance of {@link Runnable} into a new one which gets all the
     * entries from current ThreadContext map.
     *
     * @param task the instance of Runnable to wrap
     * @return the new task
     */
    public static Runnable withCurrentContext(final Runnable task) {

        final Map<String, String> currentContext = ThreadContext.getImmutableContext();
        return () -> {
            ThreadContext.putAll(currentContext);
            task.run();
        };
    }

    private LogUtils() {

        throw new AssertionError(getClass().getCanonicalName() + " is a utility class and must not be initialized");
    }
}
