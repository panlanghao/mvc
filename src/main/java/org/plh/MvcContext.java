package org.plh;

import org.plh.annotation.ReqMapping;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO:描述
 *
 * @author 潘朗豪
 * @version 1.0
 * @date 2020/6/21
 */
public class MvcContext {

    private ApplicationContext applicationContext;

    private Map<String,HandleMethod> handleMethodMap = new HashMap<>();

    public MvcContext(ApplicationContext applicationContext) {
        // 遍历spring容器的bean定义名称
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName:
             beanDefinitionNames) {
            // 根据bean定义名称找到类
            Class<?> type = applicationContext.getType(beanDefinitionName);
            // 遍历类的方法
            for (Method method:
                 type.getDeclaredMethods()) {
                // 获取ReqMapping枚举信息
                ReqMapping reqMapping = method.getAnnotation(ReqMapping.class);
                if(reqMapping!=null){
                    // 根据该方法封装一个HandleMethod
                    HandleMethod handleMethod = new HandleMethod();
                    handleMethod.setMethod(method);
                    handleMethod.setReqMapping(reqMapping);
                    handleMethod.setTargetClass(applicationContext.getBean(type));
                    handleMethodMap.put(getMappingKey(reqMapping.value()),handleMethod);
                }
            }
        }
    }

    /**
     *
     * @param str
     * @return
     */
    private String getMappingKey(String str){
        if(!str.startsWith("/")){
            str = "/"+str;
        }
        return str;
    }

    /**
     * 获取匹配的HandleMethod
     * @param url
     * @return
     */
    public HandleMethod getHandleMethod(String url){
        return handleMethodMap.get(url);
    }
}
