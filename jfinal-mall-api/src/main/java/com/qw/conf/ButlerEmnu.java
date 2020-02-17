package com.qw.conf;

/**
 * @author qw
 */
public class ButlerEmnu {
    public enum DeviceEnum {
        PC, WAP, ANDROID, IOS, WEIXIN, UNKNOW
    }

    public enum InvoiceTypeEnum {
        Normal("普通发票", 0),
        Electronic("电子发票", 1),
        ValueAdded("增值税发票", 2);

        private String name;
        private int value;

        InvoiceTypeEnum(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }
    }

    public enum BalanceAmountTypeEnum {
        ButerReward("平台推广佣金", 1),
        ShopReward("店铺推广佣金", 2),
        ReturnMoney("优惠返现", 3),
        Charge("充值", 4),
        SaleIncome("商品销售收入", 5),
        Wthdrawal("提现", 6),
        Cost("消费", 7),
        FirstProm("首单奖励", 8);

        private String name;
        private int value;

        BalanceAmountTypeEnum(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }
    }

    public enum BalanceInOutFlagEnum {
        In("收入", 1),
        Out("支出", 2);
        private String name;
        private int value;

        BalanceInOutFlagEnum(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }
    }

    public enum BalanceFrozenFlagEnum {
        Frozen("冻结", 1),
        Normal("正常", 2),
        Cancel("取消", 3);
        private String name;
        private int value;

        BalanceFrozenFlagEnum(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }
    }

    public enum AuthStatus {
        Appled("申请中", 1),
        Authed("审核通过", 2),
        NotPass("未审核通过", 3);
        private String name;
        private int value;

        AuthStatus(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }
    }

    public enum ChargePayStatus {
        WaitPay("未打款", 0),
        Paied("打款成功", 1),
        PayClose("交易关闭", 2);
        private String name;
        private int value;

        ChargePayStatus(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }
    }

    public enum WithdrawalPayStatus {
        WaitPay("未打款", 1),
        Paying("打款中", 2),
        Paied("打款成功", 3),
        PayFail("打款失败", 4);
        private String name;
        private int value;

        WithdrawalPayStatus(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }
    }

    public enum PayType {
        Ali("支付宝支付", 1),
        Wechat("微信支付", 2),
        None("纯积分支付", 3),
        Offline("线下支付", 3);
        private String name;
        private int value;

        PayType(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }
    }
}
