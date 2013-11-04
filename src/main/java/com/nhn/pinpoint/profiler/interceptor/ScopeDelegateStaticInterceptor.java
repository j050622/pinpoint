package com.nhn.pinpoint.profiler.interceptor;

import com.nhn.pinpoint.profiler.context.TraceContext;
import com.nhn.pinpoint.profiler.logging.PLogger;
import com.nhn.pinpoint.profiler.logging.PLoggerFactory;
import com.nhn.pinpoint.profiler.util.DepthScope;

/**
 * @author emeroad
 */
public class ScopeDelegateStaticInterceptor implements StaticAroundInterceptor, TraceContextSupport {
    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());
    private final boolean isTrace = logger.isTraceEnabled();
    private final StaticAroundInterceptor delegate;
    private final DepthScope scope;


    public ScopeDelegateStaticInterceptor(StaticAroundInterceptor delegate, DepthScope scope) {
        if (delegate == null) {
            throw new NullPointerException("delegate must not be null");
        }
        if (scope == null) {
            throw new NullPointerException("scope must not be null");
        }
        this.delegate = delegate;
        this.scope = scope;
    }

    @Override
    public void before(Object target, String className, String methodName, String parameterDescription, Object[] args) {
        final int push = scope.push();
        if (push != DepthScope.ZERO) {
            if (isTrace) {
                logger.trace("push {}. skip trace. level:{} {}", new Object[]{scope.getName(), push, delegate.getClass()});
            }
            return;
        }
        this.delegate.before(target, className, methodName, parameterDescription, args);
    }

    @Override
    public void after(Object target, String className, String methodName, String parameterDescription, Object[] args, Object result) {
        final int pop = scope.pop();
        if (pop != DepthScope.ZERO) {
            if (isTrace) {
                logger.trace("pop {}. skip trace. level:{} {}", new Object[]{scope.getName(), pop, delegate.getClass()});
            }
            return;
        }
        this.delegate.after(target, className, methodName, parameterDescription, args, result);
    }


    @Override
    public void setTraceContext(TraceContext traceContext) {
        if (this.delegate instanceof TraceContextSupport) {
            ((TraceContextSupport) this.delegate).setTraceContext(traceContext);
        }
    }



}
