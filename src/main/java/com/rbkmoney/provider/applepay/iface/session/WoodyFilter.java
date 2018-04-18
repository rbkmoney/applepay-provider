package com.rbkmoney.provider.applepay.iface.session;

import com.rbkmoney.woody.api.flow.WFlow;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * Created by vpankrashkin on 12.04.18.
 */
@WebFilter(filterName = "WoodyFilter", urlPatterns = "/api/v1")
public class WoodyFilter  implements Filter{
    private final WFlow wFlow = new WFlow();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        wFlow.createServiceFork(() -> {
            try {
                filterChain.doFilter(servletRequest, servletResponse);
            } catch (IOException | ServletException e) {
                sneakyThrow(e);
            }
        }).run();
    }

    @Override
    public void destroy() {

    }

    @SuppressWarnings("unchecked")
    private <E extends Throwable, T> T sneakyThrow(Throwable t) throws E {
        throw (E) t;
    }

}
