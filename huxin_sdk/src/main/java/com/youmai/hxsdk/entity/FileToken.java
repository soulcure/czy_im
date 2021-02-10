package com.youmai.hxsdk.entity;

/**
 * Created by colin on 2016/7/21.
 */
public class FileToken extends RespBaseBean {
    /**
     * d : {"fid":"355742","upToken":"WJsDTCLR8aZZiP_3tNWfxLTobOsX_WIsvcxuXHm1:i3o5YLzGEfMLpJSHwKhBr1RJ1cQ=:eyJjYWxsYmFja1VybCI6Imh0dHA6Ly90ZXN0Mi53YXAuaHV4aW4uYml6L3NlcnZpY2UvcWluaXVzZXJ2aWNlL2NhbGxiYWNrIiwic2NvcGUiOiJpaHV4aW4tdGVzdDozNTU3NDIiLCJjYWxsYmFja0JvZHkiOiJmaWxlbmFtZVx1MDAzZCQoa2V5KVx1MDAyNmZpbGVzaXplXHUwMDNkJChmc2l6ZSlcdTAwMjZtaW1lVHlwZVx1MDAzZCQobWltZVR5cGUpXHUwMDI2bXNpc2RuXHUwMDNkJCh4Om1zaXNkbilcdTAwMjZmaWRcdTAwM2QkKHg6ZmlkKVx1MDAyNnR5cGVcdTAwM2QkKHg6dHlwZSkiLCJkZWFkbGluZSI6MTUwOTYwODY2MX0="}
     */

    private DBean d;

    public DBean getD() {
        return d;
    }

    public void setD(DBean d) {
        this.d = d;
    }

    public static class DBean {
        /**
         * fid : 355742
         * upToken : WJsDTCLR8aZZiP_3tNWfxLTobOsX_WIsvcxuXHm1:i3o5YLzGEfMLpJSHwKhBr1RJ1cQ=:eyJjYWxsYmFja1VybCI6Imh0dHA6Ly90ZXN0Mi53YXAuaHV4aW4uYml6L3NlcnZpY2UvcWluaXVzZXJ2aWNlL2NhbGxiYWNrIiwic2NvcGUiOiJpaHV4aW4tdGVzdDozNTU3NDIiLCJjYWxsYmFja0JvZHkiOiJmaWxlbmFtZVx1MDAzZCQoa2V5KVx1MDAyNmZpbGVzaXplXHUwMDNkJChmc2l6ZSlcdTAwMjZtaW1lVHlwZVx1MDAzZCQobWltZVR5cGUpXHUwMDI2bXNpc2RuXHUwMDNkJCh4Om1zaXNkbilcdTAwMjZmaWRcdTAwM2QkKHg6ZmlkKVx1MDAyNnR5cGVcdTAwM2QkKHg6dHlwZSkiLCJkZWFkbGluZSI6MTUwOTYwODY2MX0=
         */

        private String fid;
        private String upToken;

        public String getFid() {
            return fid;
        }

        public void setFid(String fid) {
            this.fid = fid;
        }

        public String getUpToken() {
            return upToken;
        }

        public void setUpToken(String upToken) {
            this.upToken = upToken;
        }
    }
}
