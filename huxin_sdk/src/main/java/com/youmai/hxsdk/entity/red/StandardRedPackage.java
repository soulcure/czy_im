package com.youmai.hxsdk.entity.red;

public class StandardRedPackage {

    private int code;
    private String message;
    private ContentBean content;

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

    public ContentBean getContent() {
        return content;
    }

    public void setContent(ContentBean content) {
        this.content = content;
    }

    public static class ContentBean {
        /**
         * blessing : 大吉大利，今晚吃鸡！
         * orderDesc : 1.彩利是的支付使用的是彩饭票<br/>
         2.可在”彩利是-利是明细“中查看领取情况<br/>
         3.若发送至微信过程中有问题，可前往”彩利是-利是明细“点击”发送不成功？“继续发给微信好友<br/>
         4.未被抢光的饭票将会在24小时后退回“我的饭票”
         * fixedConfig : {"lsType":1,"moneyMax":2000,"moneyMin":0.1,"numberMax":100,"numberMin":1}
         * randomConfig : {"lsType":2,"moneyMax":2000,"moneyMin":0.1,"numberMax":100,"numberMin":1}
         */

        private String blessing;
        private String orderDesc;
        private FixedConfigBean fixedConfig;
        private RandomConfigBean randomConfig;

        public String getBlessing() {
            return blessing;
        }

        public void setBlessing(String blessing) {
            this.blessing = blessing;
        }

        public String getOrderDesc() {
            return orderDesc;
        }

        public void setOrderDesc(String orderDesc) {
            this.orderDesc = orderDesc;
        }

        public FixedConfigBean getFixedConfig() {
            return fixedConfig;
        }

        public void setFixedConfig(FixedConfigBean fixedConfig) {
            this.fixedConfig = fixedConfig;
        }

        public RandomConfigBean getRandomConfig() {
            return randomConfig;
        }

        public void setRandomConfig(RandomConfigBean randomConfig) {
            this.randomConfig = randomConfig;
        }

        public static class FixedConfigBean {
            /**
             * lsType : 1
             * moneyMax : 2000
             * moneyMin : 0.1
             * numberMax : 100
             * numberMin : 1
             */

            private int lsType;
            private double moneyMax;
            private double moneyMin;
            private int numberMax;
            private int numberMin;

            public int getLsType() {
                return lsType;
            }

            public void setLsType(int lsType) {
                this.lsType = lsType;
            }

            public double getMoneyMax() {
                return moneyMax;
            }

            public void setMoneyMax(double moneyMax) {
                this.moneyMax = moneyMax;
            }

            public double getMoneyMin() {
                return moneyMin;
            }

            public void setMoneyMin(double moneyMin) {
                this.moneyMin = moneyMin;
            }

            public int getNumberMax() {
                return numberMax;
            }

            public void setNumberMax(int numberMax) {
                this.numberMax = numberMax;
            }

            public int getNumberMin() {
                return numberMin;
            }

            public void setNumberMin(int numberMin) {
                this.numberMin = numberMin;
            }
        }

        public static class RandomConfigBean {
            /**
             * lsType : 2
             * moneyMax : 2000
             * moneyMin : 0.1
             * numberMax : 100
             * numberMin : 1
             */

            private int lsType;
            private int moneyMax;
            private double moneyMin;
            private int numberMax;
            private int numberMin;

            public int getLsType() {
                return lsType;
            }

            public void setLsType(int lsType) {
                this.lsType = lsType;
            }

            public int getMoneyMax() {
                return moneyMax;
            }

            public void setMoneyMax(int moneyMax) {
                this.moneyMax = moneyMax;
            }

            public double getMoneyMin() {
                return moneyMin;
            }

            public void setMoneyMin(double moneyMin) {
                this.moneyMin = moneyMin;
            }

            public int getNumberMax() {
                return numberMax;
            }

            public void setNumberMax(int numberMax) {
                this.numberMax = numberMax;
            }

            public int getNumberMin() {
                return numberMin;
            }

            public void setNumberMin(int numberMin) {
                this.numberMin = numberMin;
            }
        }
    }
}
