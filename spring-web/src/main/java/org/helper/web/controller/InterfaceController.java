package org.helper.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class InterfaceController {
    private static final Logger logger = LoggerFactory.getLogger(InterfaceController.class);


    // 处理所有未知的和静态资源的请求
    @RequestMapping(value = "/")
    public String index() {
        return "redirect:/task/list";
    }

}
