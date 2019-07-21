package com.can.store.shopping.commons.kiss;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @copyright 中山市五象中土科技有限公司
 * @auth kj.林
 * @time 2019-07-06 23:44
 */
public class Config {
    private static Map<String, Object> jvm_args;
    private static Map<String, String> server_list;

    static {
        server_list = new HashMap<>();
        server_list.put("localhost", "prod-01");
    }

    ;

    private static boolean debugFlag = true;

    private static Properties ApplicationProperties;

    public static boolean isDebugFlag() {
        return debugFlag;
    }


    public String getMysqlDriver() {
        return Config.getProperty("spring.datasource.driver-class-name");
    }

    public String getMysqlUrl() {
        return Config.getProperty("spring.datasource.url");
    }

    public String getMysqlUser() {
        return Config.getProperty("spring.datasource.username");
    }

    public String getMysqlPassword() {
        return Config.getProperty("spring.datasource.password");
    }

    /**
     * 根据mysql服务器的实际情况修改此值，应该小于wait_timeout
     *
     * @return
     */
    public int getMysqlWaitTimeout() {
        String value = Config.getProperty("env.mysql-wait_timeout");
        if (null == value) {
            return 500;
        }

        return Integer.parseInt(value);
    }

    public String getRedisHost() {
        return Config.getProperty("spring.redis.host");
    }

    public String getRedisPassword() {
        return Config.getProperty("spring.redis.password");
    }

    public int getRedisPort() {
        String port = Config.getProperty("spring.redis.port");
        if (null == port) {
            return -1;
        }

        return Integer.parseInt(port);
    }

    /**
     * @param key 获取配置数据
     * @return
     */
    public static String getProperty(String key) {
        return ApplicationProperties.getProperty(key);
    }

    /**
     * getResource()方法会去classpath下找这个文件，获取到url resource, 得到这个资源后，调用url.getFile获取到 文件 的绝对路径
     */
    public static String getPropertyFilePath(String key) {
        String classFilePath = getProperty(key);
        try {
            return ResourceUtils.getFile(classFilePath).getPath();
        } catch (FileNotFoundException e) {
            return null;
        }

    }

    public static void setJvmArgs(Map<String, Object> args) {
        jvm_args = args;
    }

    public static Object getJvmArgs(String key) {
        if (null == key || null == jvm_args) {
            return null;
        }
        return jvm_args.get(key);
    }

    public Map<String, String> getWxPayJSAPI() {
        Map<String, String> config = new HashMap<>();

//        config.put("appid", Config.getProperty("wxpay.appid"));
//        config.put("mch_id", Config.getProperty("wxpay.mch_id"));
//        config.put("spbill_create_ip", Config.getProperty("wxpay.spbill_create_ip"));
//        config.put("notify_url", Config.getProperty("wxpay.notify_url"));
//        config.put("trade_type", Config.getProperty("wxpay.trade_type"));
//        config.put("signType", Config.getProperty("wxpay.signType"));
//        config.put("createOrder_url", Config.getProperty("wxpay.createOrder_url"));
//        config.put("apiCert", Config.getProperty("wxpay.apiCert"));
//        config.put("refund_url", Config.getProperty("wxpay.refund_url"));
        return config;
    }

    public static String getHostNameForLiunx() {
        try {
            return (InetAddress.getLocalHost()).getHostName();
        } catch (UnknownHostException uhe) {
            String host = uhe.getMessage(); // host = "hostname: hostname"
            if (host != null) {
                int colon = host.indexOf(':');
                if (colon > 0) {
                    return host.substring(0, colon);
                }
            }
            return "UnknownHost";
        }
    }

    public static String getServerEnvTypeName() {
        String hostname = getHostNameForLiunx();
        String env_name = server_list.get(hostname);
        if (null == env_name) {
            return "local";
        }
        return env_name;
    }

    public boolean isProductionEnv() {
        String value = Config.getProperty("env.app-env");
        if (null == value) {
            return false;
        }

        return value.equals("production");
    }

    public static boolean isDevServer() {
        String name = getServerEnvTypeName();
        return null != name && name.equals("dev");
    }

    public static boolean isProductionServer() {
        String name = getServerEnvTypeName();
        if (null == name) {
            return false;
        }

        String k = "production";
        if (k.length() <= name.length()) {
            return name.substring(0, k.length()).equals(k);
        }

        return false;
    }

    protected static void appAutoConfig() {
        String app_url = ApplicationProperties.getProperty("app_url");
        if (!app_url.substring(app_url.length() - 1).equals("/")) {
            app_url += "/";
        }
        ApplicationProperties.setProperty("app_url", app_url);
    }

    public static void bootstrap() {
        System.setProperty("user.timezone", "Asia/Shanghai");
    }

    public static boolean startChecking() throws Exception {
        bootstrap();

        // 获取application.yml配置数据
        YamlPropertiesFactoryBean yamlMapFactoryBean = new YamlPropertiesFactoryBean();
        // 可以加载多个yml文件
        yamlMapFactoryBean.setResources(new ClassPathResource("application.yml"));
        ApplicationProperties = yamlMapFactoryBean.getObject();

        // 重写部分配置参数，以便使用时更方便
        appAutoConfig();

        String env_debug = ApplicationProperties.getProperty("env.debug");
        debugFlag = (null != env_debug) ? (env_debug.equals("true") ? true : false) : false;

        String hostname = getHostNameForLiunx();

        //本地调试生产环境用
        //if(true){return true;}

        Config configIns = new Config();
        String env_name = server_list.get(hostname);
        if (null != env_name) {
            String n = "production";
            if (n.length() <= env_name.length()) {
                if (env_name.substring(0, "production".length()).equals("production")) {
                    if (configIns.isProductionEnv()) {
                        return true;
                    }
                    throw new Exception("服务器环境和配置数据不一致");
                }
            }

            String n1 = "dev";
            if (n1.length() <= env_name.length()) {
                if (env_name.substring(0, "dev".length()).equals("dev")) {
                    if (configIns.isProductionEnv()) {
                        throw new Exception("服务器环境和配置数据不一致");
                    }
                    return true;
                }
            }
        }

        InetAddress candidateAddress = null;
        try {
            // 遍历所有的网络接口，如果所有ip地址都是127.0.0.1，视为本地主机。
            // 如果非本地主机且不在server_list列表内但标识为production的，抛出异常。
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements(); ) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                // 在所有的接口下再遍历IP
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements(); ) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {// 排除loopback类型地址
                        if (inetAddr.isSiteLocalAddress()) {
                            // 如果是site-local地址，本地主机。
                            if (configIns.isProductionEnv()) {
                                //本地主机如需使用生产环境，应该手动注释抛异常
                                throw new Exception("本地主机访问生产服务器");
                            }
                            return true;
                        } else if (candidateAddress == null) {
                            // site-local类型的地址未被发现，先记录候选地址
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (configIns.isProductionEnv()) {
            throw new Exception("服务器环境和配置数据不一致");
        }
        return true;
    }
}
