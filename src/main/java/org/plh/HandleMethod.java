package org.plh;

import org.plh.annotation.ReqMapping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * TODO:描述
 *
 * @author 潘朗豪
 * @version 1.0
 * @date 2020/6/21
 */
public class HandleMethod {

    /**
     * 待执行的方法
     */
    private Method method;

    /**
     * 目标类
     */
    private Object targetClass;

    /**
     * 枚举信息
     */
    private ReqMapping reqMapping;

    public Method getMethod() {
        return method;
    }

    public Object getTargetClass() {
        return targetClass;
    }

    public ReqMapping getReqMapping() {
        return reqMapping;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public void setTargetClass(Object targetClass) {
        this.targetClass = targetClass;
    }

    public void setReqMapping(ReqMapping reqMapping) {
        this.reqMapping = reqMapping;
    }

    /**
     * 执行方法
     * @param args
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public Object run(Object... args) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(targetClass,args);
    }
}
