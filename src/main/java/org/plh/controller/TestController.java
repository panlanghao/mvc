package org.plh.controller;

import org.plh.annotation.ReqMapping;
import org.plh.view.FreemarkerView;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO:描述
 *
 * @author 潘朗豪
 * @version 1.0
 * @date 2020/6/23
 */
@Controller
public class TestController {

    @ReqMapping("getDetail")
    public FreemarkerView getDetail(String name,Integer age){
        FreemarkerView freemarkerView = new FreemarkerView();
        freemarkerView.setFtlPath("detail.ftl");
        Map<String,Object> model = new HashMap<>();
        model.put("name",name);
        model.put("age",age);
        model.put("company","潘朗豪公司");
        freemarkerView.setModel(model);
        return freemarkerView;
    }
}
