package com.server_auth;

public class RouterInfo {
    public static class FW300R {
        // 验证接口 过去的验证地址是authorisation
        public static String sHost = "http://192.168.1.1";
        private static final String sReleaseHost = "/userRpm/StatusRpm.htm";
        public static String getsReleaseHost() {
            return "http://" + sHost + sReleaseHost;
        }
        private static final String sRenewHost = "/userRpm/StatusRpm.htm";

        public static String getsRenewHost() {
            return "http://" + sHost + sRenewHost;
        }
    }
}
