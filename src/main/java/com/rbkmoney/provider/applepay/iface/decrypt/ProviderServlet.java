package com.rbkmoney.provider.applepay.iface.decrypt;

import com.rbkmoney.damsel.payment_tool_provider.PaymentToolProviderSrv;
import com.rbkmoney.woody.thrift.impl.http.THServiceBuilder;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;

/**
 * Created by vpankrashkin on 18.04.18.
 */
@WebServlet("/provider/apple")
public class ProviderServlet extends GenericServlet {
    private final Servlet handlerServlet;

    public ProviderServlet(PaymentToolProviderSrv.Iface handler) {
        this.handlerServlet = new THServiceBuilder()
                .build(PaymentToolProviderSrv.Iface.class, handler);
    }

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        handlerServlet.service(req, res);
    }


}
