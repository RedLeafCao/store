package com.can.store.shopping.commons.kiss.deprecated;

/**
 * @copyright 中山市五象中土科技有限公司
 * @auth kj.林
 * @time 2019-07-06 23:44
 */
public enum BankCodeEnum {
    CODE_1001(1001, "招商银行", "CMB"),
    CODE_1002(1002, "工商银行", "ICBC"),
    CODE_1003(1003, "建设银行", "CCB"),
    CODE_1004(1004, "浦发银行", "SPDB"),
    CODE_1005(1005, "农业银行", "ABC"),
    CODE_1006(1006, "民生银行", "CMBC"),
    CODE_1009(1009, "兴业银行", "CIB"),
    CODE_1010(1010, "平安银行", "SPABANK"),
    CODE_1020(1020, "交通银行", "COMM"),
    CODE_1021(1021, "中信银行", "CITIC"),
    CODE_1022(1022, "光大银行", "CEB"),
    CODE_1025(1025, "华夏银行", "HXBANK"),
    CODE_1026(1026, "中国银行", "BOC"),
    CODE_1027(1027, "广发银行", "GDB"),
    CODE_1032(1032, "北京银行", "BJRCB"),
    CODE_1056(1056, "宁波银行", "NBBANK"),
    CODE_1066(1066, "邮政储蓄银行", "PSBC");

    private final String name;
    private final String code;
    private final int number;

    private BankCodeEnum(int num, String n, String c) {
        this.number = num;
        this.name = n;
        this.code = c;
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public int getNumber() {
        return this.number;
    }
}
