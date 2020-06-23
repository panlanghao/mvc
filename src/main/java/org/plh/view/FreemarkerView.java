package org.plh.view;

import java.util.HashMap;
import java.util.Map;

/**
 * TODO:描述
 *
 * @author 潘朗豪
 * @version 1.0
 * @date 2020/6/22
 */
public class FreemarkerView {
    /**
     * 路径
     */
    private String ftlPath;
    /**
     *
     */
    private Map<String,Object> model = new HashMap<>();

    public String getFtlPath() {
        return ftlPath;
    }

    public void setFtlPath(String ftlPath) {
        this.ftlPath = ftlPath;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void setModel(Map<String, Object> model) {
        this.model = model;
    }
}
