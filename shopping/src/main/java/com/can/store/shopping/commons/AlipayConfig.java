package com.can.store.shopping.commons;

public  class AlipayConfig {
    // 沙箱
    // APPID,应用id,也是支付宝收款账户
    public static final String APP_ID = "2016100100640000";
    // 商户秘钥
    public static final String MERCHANT_PRIVATE_KEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCrbVq4f7G4GuS2p+QFt5+NdMcEYK22ZGItzFXMfC8T+cLtaGzJK+bicZLaxPXhSMTQ9PPBO9elmyGxCwZQt3FlTgyW5Lr6w2DuGxKW/tR1leCiCIEuJKSwfW1Mb0+2lIOukfKQd2p8Ic8Zc4JEFjg+KKRRpy1LeYSldfjv5veXzxr58zgaWFOBWqpr763nuUBKYAB0p7WpHie2zF5ovhZCyUvi1orliOtNAdIICs1rrMI2zk0Z1kO7AsP0azuiZrISNBU9i4gb65pNXslARnRUnoIPMFDtTJeEIrHi2kjq8qttVRGnit2Jd7PP++fxxDn1Kb8UVRjw0w/eQSKnb9TJAgMBAAECggEAXXHCngedTIn8WkSyWxt48ZbSVWSNzHpzuhtEOa8Hyo++3cKqag5wUMcwEeiDT0ZnYq/jn1WLe05Q0zz7OEyGl34wBqCv+7k8wQ4o1/4Yue/0/t477Hc0/q+gQKX8gb36+dG8s1skxH15cpowTAUAq4Fj8M58NkQLNlZUWBFCrZDm8qWfBNJV1CiihjAl3ywuKB1XISfEvuj+wLaPe7qFioxPbn3UA9YQZWJweg/HUtWAUIf7wwBJqLKSSkMV+QO0tvkgit0xjEfE1CBRYN3Rn15VRlDGj2LLHOkwdQSEN34tvwbpiMZxHBXrtrIDsEcXSYM8vyzkmEbE23u8aPfs8QKBgQDTPsfg/0u3T1hL0JQJ1wPvLVGkcI6xcuWERKtIT2HriTJCFSLwtQEM2m8/eWzfS/4fDaf1LN/+97nAzr0XELPTNnCGFbbNqoRSpQUcUITIO6LO4R/RcfkN79ZpeNAeDW1cbqWvN1O3gMPQPanhZN1f0lGPIInsm8MsprfAnaIk1wKBgQDPvvpBVP+iinpeCsGvXSQYLyzrv8Uo+AHoFZd/Kmaex1XmJ2IBim95WlS6/6qtAjWfiNQpPSoAtPro2ywQIX63zHGrRnXndsETsK7w1WQm250LzhT98nHupsFsdT2Nsw1IXWMMN/n3NQ2b/h01g7zrYzwfigLP4cAiuH4eYr7/XwKBgDBN6NLQTfYK1LvxZAij/ChTCpo5iseU6FrIgQo17MQ5SR7HSogUO7s+r7WqBHVDIUbkjoWHz2i0KOun2qFNlnEzv6NJj9WTqlJjnbmNx+8pg98ep3HzR+oh77uKfryAgYNjYcuhMSmC7kPN9eyubdiddTzvSdqq+4RYUyheY1ddAoGAZdr4z/dgrsy+WkVj2RsNi1315FBN1lxicuvDL3IWhlqtXv6IKsE5DoKfcF/Ryql+qQ1pGB8I7PfO3UQ4QmBpxKvUP2l4fVfYKlNCMootZG/ge906pZetDJlt1ENXPpY/OO+wlFkNkYrNdWIN6CMDCVXKMJx17H5+uYdDLpqzknkCgYA+KxF/qvkYUaYLMUx1b0XyC2yy7f/2yEuI9pvvY89Y5nS9ijvIzh13ZqqeZK+/0JsbZh6bay0sxdbvPbxwEdAlNRehA2muG4LPipO9rBjCxq83gcvM26q1z13vgd2S9WCnk8M7zy+Sa6W9ailo0algqObQPT8H93kxUoRnpmUtpw==";
    // 支付宝公钥
    public static final String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAymw5hBI+nyef5Ct9hl+Sg48yVynu+zhb6Tp66Hwi5dSk/d9E/yp8tODu5j2hM7iGbOWoHoP3+yLxAq+kamA7HbbWlY7jqc0mC0YdCkrkW0MXBDYuuSgu9gzgSJ6A7NJSh0oeWikDgWCsN+JNZTGnCTTcRhix/rTZNl8hQD63CKFhKC4bDRrpkYd1jYhdeCqnxrphMDMkoGSUlFPwrDwIg3djvYiG/3v2CIKe8pnc9JkDUdfYX5BGEQE6h4qeueT1LkcZT7Oa685H7KAIKaAdFr/z2IksQY4naRjHpExp4Ks4jolgjgCT9wa+AANqYULxqLbviLE41f0kDWFBA2DcOQIDAQAB";
    // 编码格式
    public static final String CHARSET = "UTF_8";
    // 支付宝网关路径
    public static final String GATEWAY_URL = "https://openapi.alipaydev.com/gateway.do";
    // 服务器异步通知路径,调用本项目的方法
    public static final String NOTIFY_URL = "";
    // 支付宝同步通知路径，当付款完毕后跳转本项目的页面
    public static final String RETURN_URL = "";
    // 格式
    public static  final String FORMAT = "JSON";
    // 签名方式
    public static final String SIGN_TYPE = "RSA2";
}
