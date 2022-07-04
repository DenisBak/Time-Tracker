package com.denis.control.filters;

import jakarta.servlet.*;

import java.io.IOException;

public class ServerRestartedFilter implements Filter {
    private boolean serverRestarted;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//            Runtime.getRuntime().exec("sh /home/denis/sh.sh");
//            Runtime.getRuntime().exec("sh /home/denis/st.sh");
        if (serverRestarted) {
        }
    }
}
