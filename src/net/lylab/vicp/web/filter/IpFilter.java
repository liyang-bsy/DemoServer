package net.lylab.vicp.web.filter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.vicp.lylab.core.CoreDef;
import net.vicp.lylab.utils.Utils;

/**
 * 该过滤器用于过滤非指定列表中的IP不能访问指定功能权限。
 * 
 * @author <a href="mailto:luzhich@cn.ibm.com">Lucas</a>
 * 
 * @version 1.0 2011-11-29
 */

public class IpFilter implements Filter {

//	// 允许的IP访问列表，暂时不用
//	private String[] ipList;

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest arg0, ServletResponse arg1,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) arg0;
        HttpServletResponse response = (HttpServletResponse) arg1;

        // 获取请求IP
        InetAddress inet = null;
        String ip = request.getRemoteAddr();
        try {
            inet = InetAddress.getLocalHost();
            if (ip.equals("127.0.0.1"))
                ip = inet.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // 不在IP白名单里，
        if (!Utils.inList(CoreDef.config.getString("ipWhiteList").split("\\,"), ip)) {
            response.getOutputStream()
                    .write((ip + " ip forbidden.").getBytes());
            response.getOutputStream().flush();
            return;
        }

        // 其它继续
        chain.doFilter(arg0, arg1);

    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    }

}