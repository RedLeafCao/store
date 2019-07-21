package com.can.store.shopping.commons.kizz.http.response;

/**
 * @copyright 中山市五象中土科技有限公司
 * @auth kj.林
 * @time 2019-07-06 23:44
 */
public class Abstract {
    //@ApiModelProperty("状态消息")
    public String message;

    //@ApiModelProperty("接口状态码: 0成功，大于0错误")
    public int status;

    //@ApiModelProperty("调试错误码: 0成功，大于0错误")
    public int code;

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public int getCode() {
        return this.code;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public boolean assertSuccess() {
        return 0 == getStatus();
    }
}
