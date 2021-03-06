package com.chian.mqclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;


public final class HttpUtils {
    protected static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
    /**
     * 构造函数.
     */
    private HttpUtils() {
    }

    public static String getIp2(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if(!StringUtils.isEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if(index != -1){
                return ip.substring(0,index);
            }else{
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if(!StringUtils.isEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)){
            return ip;
        }
        return request.getRemoteAddr();
    }

    /**
     * 获取客户端IP地址.<br>
     * 支持多级反向代理
     *
     * @param request
     *            HttpServletRequest
     * @return 客户端真实IP地址
     */
    public static String getRemoteAddr(final HttpServletRequest request) {
        try{
            String remoteAddr = request.getHeader("X-Forwarded-For");
            // 如果通过多级反向代理，X-Forwarded-For的值不止一个，而是一串用逗号分隔的IP值，此时取X-Forwarded-For中第一个非unknown的有效IP字符串
            if (isEffective(remoteAddr) && (remoteAddr.indexOf(",") > -1)) {
                String[] array = remoteAddr.split(",");
                for (String element : array) {
                    if (isEffective(element)) {
                        remoteAddr = element;
                        break;
                    }
                }
            }
            if (!isEffective(remoteAddr)) {
                remoteAddr = request.getHeader("X-Real-IP");
            }
            if (!isEffective(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
            return remoteAddr;
        }catch(Exception e){
            logger.error("get romote ip error,error message:"+e.getMessage());
            return "";
        }
    }

    /**
     * 获取客户端源端口
     * @param request
     * @return
     */
    public static Long getRemotePort(final HttpServletRequest request){
        try{
            String port = request.getHeader("remote-port");
            if(! StringUtils.isEmpty(port )) {
                try{
                    return Long.parseLong(port);
                }catch(NumberFormatException ex){
                    logger.error("convert port to long error , port:	"+port);
                    return 0l;
                }
            }else{
                return 0l;
            }
        }catch(Exception e){
            logger.error("get romote port error,error message:"+e.getMessage());
            return 0l;
        }
    }

    /**
     * 远程地址是否有效.
     *
     * @param remoteAddr
     *            远程地址
     * @return true代表远程地址有效，false代表远程地址无效
     */
    private static boolean isEffective(final String remoteAddr) {
        boolean isEffective = false;
        if ((null != remoteAddr) && (!"".equals(remoteAddr.trim()))
                && (!"unknown".equalsIgnoreCase(remoteAddr.trim()))) {
            isEffective = true;
        }
        return isEffective;
    }
}