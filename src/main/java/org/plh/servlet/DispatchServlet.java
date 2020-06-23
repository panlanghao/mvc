package org.plh.servlet;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.plh.HandleMethod;
import org.plh.MvcContext;
import org.plh.view.FreemarkerView;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * TODO:描述
 *
 * @author 潘朗豪
 * @version 1.0
 * @date 2020/6/21
 */
public class DispatchServlet extends HttpServlet {
    final ParameterNameDiscoverer parameterUtil = new LocalVariableTableParameterNameDiscoverer();
    private MvcContext mvcContext;
    private Configuration freemarkerConfig;

    /**
     * 如果重写含参init方法，需要先调用父类的含参init方法
     *
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        System.out.println("init====================================");
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
        mvcContext = new MvcContext(webApplicationContext);
        Configuration configuration = null;
        try {
            configuration = webApplicationContext.getBean(Configuration.class);
        } catch (NoSuchBeanDefinitionException e) {
        }
        if (null == configuration) {
            configuration = new Configuration(Configuration.VERSION_2_3_30);
            configuration.setDefaultEncoding("UTF-8");
            configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            //设置FreeMarker的模版文件位置
            try {
                configuration.setDirectoryForTemplateLoading(new File(getServletContext().getRealPath("/ftl/")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.freemarkerConfig = configuration;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doService(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doService(req, resp);
    }

    private void doService(HttpServletRequest req, HttpServletResponse resp) {
        // 访问路径
        String uri = req.getServletPath();
        HandleMethod handleMethod = mvcContext.getHandleMethod(uri);
        if (handleMethod == null) {
            throw new IllegalArgumentException(String.format("not found %s mapping", uri));
        }
        // 获取方法的入参
        List<String> paramNames = Arrays.asList(parameterUtil.getParameterNames(handleMethod.getMethod()));
        Class<?>[] paramTypes = handleMethod.getMethod().getParameterTypes();
        Object[] args = new Object[paramNames.size()];

        for (int i = 0; i < paramNames.size(); i++) {
            if (paramTypes[i].isAssignableFrom(HttpServletRequest.class)) {
                args[i] = req;
            } else if (paramTypes[i].isAssignableFrom(HttpServletResponse.class)) {
                args[i] = resp;
            } else {
                String parameterValue = req.getParameter(paramNames.get(i));
                args[i] = this.convent(parameterValue, paramTypes[i]);
            }
        }
        try {
            // 执行匹配到的方法
            Object result = handleMethod.run(args);
            // 适配结果
            this.processResult(result, resp);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }

    }

    private void processResult(Object result, HttpServletResponse resp) throws IOException, TemplateException {
        if (result instanceof FreemarkerView) {
            FreemarkerView freemarkerView = (FreemarkerView) result;
            Template temp = freemarkerConfig.getTemplate(freemarkerView.getFtlPath());
            resp.setContentType("text/html; charset=utf-8");
            resp.setCharacterEncoding("utf-8");
            resp.setStatus(200);
            temp.process(freemarkerView.getModel(), resp.getWriter());
        }
    }

    /**
     * @param paramValue
     * @param tClass
     * @param <T>
     * @return
     */
    private <T> T convent(String paramValue, Class<T> tClass) {
        Object object = null;
        if (null == paramValue) {
            return null;
        } else if (Integer.class.equals(tClass)) {
            object = Integer.valueOf(paramValue);
        } else if (Long.class.equals(tClass)) {
            object = Long.valueOf(paramValue);
        } else if (BigDecimal.class.equals(tClass)) {
            object = new BigDecimal(paramValue);
        } else if (String.class.equals(tClass)) {
            object = paramValue;
        } else {
            System.err.println(String.format("not support param type %s", tClass.getName()));
        }

        return (T) object;
    }
}
