package com.youmai.hxsdk.entity;

import java.util.List;

public class SearchResult {


    /**
     * code : 0
     * message : 查找成功
     * content : [{"username":"lina010","password":"","mobile":"","email":"","name":"李娜","landline":"","status":0,"accountUuid":"14a684fc-73fb-4498-ac70-d31a305da5aa","corpId":"a8c58297436f433787725a94f780a3c9","sex":"1","dr":"0","updateTs":"2018-05-19 01:27:55","createTs":"2018-05-02 05:24:38","salaryLevel":"","czyId":"204217","jobType":"产品经理","jobUuid":"079060fb-d5f8-462f-a1a7-cf876e758795","orgUuid":"3798839c-4206-4fbb-9c2b-4000f31080ec","orgName":"华北区域客户部","createtime":"2018-07-24 20:30:24","isFavorite":0,"Favoriteid":0},{"username":"linali","password":"","mobile":"18691713888","email":"","name":"李娜","landline":"","status":0,"accountUuid":"69c3cb7e-f336-4d44-95bb-c88b1d53408a","corpId":"a8c58297436f433787725a94f780a3c9","sex":"1","dr":"0","updateTs":"2017-12-11 14:24:35","createTs":"2017-06-27 03:48:42","salaryLevel":"","czyId":"182604","jobType":"产品公司负责人","jobUuid":"ad79a20a-f542-49c7-a3e7-2d1c51d03a5c","orgUuid":"bb2e81e0-ec3f-4bf8-9a0c-04902065370c","orgName":"智家","createtime":"2018-07-24 20:30:24","isFavorite":0,"Favoriteid":0},{"username":"linamyj","password":"","mobile":"15169696787","email":"","name":"李娜","landline":"","status":0,"accountUuid":"80742c08-1f50-45ed-99f0-2a7d278f3776","corpId":"a8c58297436f433787725a94f780a3c9","sex":"2","dr":"0","updateTs":"2018-05-18 05:50:52","createTs":"2017-06-07 01:05:32","salaryLevel":"","czyId":"179584","jobType":"客服助理","jobUuid":"c5f970f4-e970-4fdf-a3a3-1d70c46f42e2","orgUuid":"f27aeeef-f3e2-498b-90d8-1493bbbaa310","orgName":"潍坊项目","createtime":"2018-07-24 20:30:24","isFavorite":0,"Favoriteid":0},{"username":"lina1993","password":"","mobile":"13607948654","email":"","name":"李娜","landline":"","status":0,"accountUuid":"8c541d84-e532-4de1-b817-b22f676b2f45","corpId":"a8c58297436f433787725a94f780a3c9","sex":"2","dr":"0","updateTs":"2018-06-12 02:43:28","createTs":"2017-02-17 06:31:19","salaryLevel":"","czyId":"167178","jobType":"收费员","jobUuid":"49d61e3d-7001-4bcc-b35c-3627ffe2205c","orgUuid":"d860fe3d-82e9-45ec-b80f-c6a509e10d6f","orgName":"滨江豪园","createtime":"2018-07-24 20:30:24","isFavorite":0,"Favoriteid":0},{"username":"lina401","password":"","mobile":"13718477862","email":"","name":"李娜","landline":"","status":0,"accountUuid":"966d287f-13a7-4190-8bf5-df63921cc17e","corpId":"a8c58297436f433787725a94f780a3c9","sex":"1","dr":"0","updateTs":"2018-05-21 07:06:22","createTs":"2018-05-21 07:06:21","salaryLevel":"","czyId":"427011","jobType":"客服员","jobUuid":"8cacdaef-a150-4945-8801-76414ef3e115","orgUuid":"4ea399b2-8bc7-434c-81a7-8b9eea18c2da","orgName":"红橡墅(别墅二期)","createtime":"2018-07-24 20:30:24","isFavorite":0,"Favoriteid":0},{"username":"bjlina","password":"","mobile":"15291793593","email":"123456789@qq.com","name":"李娜","landline":"","status":0,"accountUuid":"f7076831-87ac-4088-9c63-17eac3306fad","corpId":"a8c58297436f433787725a94f780a3c9","sex":"2","dr":"0","updateTs":"2018-07-04 13:20:21","createTs":"2018-06-30 07:13:48","salaryLevel":"","czyId":"206724","jobType":"客户经理","jobUuid":"7d9425d4-dacd-4646-8cd5-1ef5d4f1d451","orgUuid":"51f731ec-a722-4c61-9090-d4f1ed176e64","orgName":"渭滨区机关家属楼","createtime":"2018-07-24 20:30:24","isFavorite":0,"Favoriteid":0}]
     */

    private int code;
    private String message;
    private List<ContentBean> content;

    public boolean isSuccess() {
        return code == 0;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ContentBean> getContent() {
        return content;
    }

    public void setContent(List<ContentBean> content) {
        this.content = content;
    }

    public static class ContentBean {
        /**
         * username : lina010
         * password :
         * mobile :
         * email :
         * name : 李娜
         * landline :
         * status : 0
         * accountUuid : 14a684fc-73fb-4498-ac70-d31a305da5aa
         * corpId : a8c58297436f433787725a94f780a3c9
         * sex : 1
         * dr : 0
         * updateTs : 2018-05-19 01:27:55
         * createTs : 2018-05-02 05:24:38
         * salaryLevel :
         * czyId : 204217
         * jobType : 产品经理
         * jobUuid : 079060fb-d5f8-462f-a1a7-cf876e758795
         * orgUuid : 3798839c-4206-4fbb-9c2b-4000f31080ec
         * orgName : 华北区域客户部
         * createtime : 2018-07-24 20:30:24
         * isFavorite : 0
         * Favoriteid : 0
         */

        private String username;
        private String password;
        private String mobile;
        private String email;
        private String name;
        private String landline;
        private int status;
        private String accountUuid;
        private String corpId;
        private String sex;
        private String dr;
        private String updateTs;
        private String createTs;
        private String salaryLevel;
        private String czyId;
        private String jobType;
        private String jobUuid;
        private String orgUuid;
        private String orgName;
        private String createtime;
        private int isFavorite;
        private int Favoriteid;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLandline() {
            return landline;
        }

        public void setLandline(String landline) {
            this.landline = landline;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getAccountUuid() {
            return accountUuid;
        }

        public void setAccountUuid(String accountUuid) {
            this.accountUuid = accountUuid;
        }

        public String getCorpId() {
            return corpId;
        }

        public void setCorpId(String corpId) {
            this.corpId = corpId;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getDr() {
            return dr;
        }

        public void setDr(String dr) {
            this.dr = dr;
        }

        public String getUpdateTs() {
            return updateTs;
        }

        public void setUpdateTs(String updateTs) {
            this.updateTs = updateTs;
        }

        public String getCreateTs() {
            return createTs;
        }

        public void setCreateTs(String createTs) {
            this.createTs = createTs;
        }

        public String getSalaryLevel() {
            return salaryLevel;
        }

        public void setSalaryLevel(String salaryLevel) {
            this.salaryLevel = salaryLevel;
        }

        public String getCzyId() {
            return czyId;
        }

        public void setCzyId(String czyId) {
            this.czyId = czyId;
        }

        public String getJobType() {
            return jobType;
        }

        public void setJobType(String jobType) {
            this.jobType = jobType;
        }

        public String getJobUuid() {
            return jobUuid;
        }

        public void setJobUuid(String jobUuid) {
            this.jobUuid = jobUuid;
        }

        public String getOrgUuid() {
            return orgUuid;
        }

        public void setOrgUuid(String orgUuid) {
            this.orgUuid = orgUuid;
        }

        public String getOrgName() {
            return orgName;
        }

        public void setOrgName(String orgName) {
            this.orgName = orgName;
        }

        public String getCreatetime() {
            return createtime;
        }

        public void setCreatetime(String createtime) {
            this.createtime = createtime;
        }

        public int getIsFavorite() {
            return isFavorite;
        }

        public void setIsFavorite(int isFavorite) {
            this.isFavorite = isFavorite;
        }

        public int getFavoriteid() {
            return Favoriteid;
        }

        public void setFavoriteid(int Favoriteid) {
            this.Favoriteid = Favoriteid;
        }
    }
}
