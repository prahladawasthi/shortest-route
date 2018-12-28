package com.shortestroute.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import com.shortestroute.helper.ErrorJsonMapper;

@Controller
public class ValidationController implements ErrorController {

    private static final String PATH = "/error";

    private ErrorAttributes errorAttributes;

    @Autowired
    public ValidationController(ErrorAttributes errorAttributes) {
        Assert.notNull(errorAttributes, "ErrorAttributes must not be null");
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping(value = PATH)
    public String error(HttpServletRequest request, HttpServletResponse response, Model model) {
        ErrorJsonMapper errorJsonMapper = new ErrorJsonMapper(response.getStatus(), getErrorAttributes(request, getTraceParameter(request)));
        String message = "The application has encountered an error. Please restart again.";
        model.addAttribute("validationMessage", message);
        model.addAttribute("errorCode", errorJsonMapper.getError());
        model.addAttribute("errorMessage", errorJsonMapper.getMessage());
        model.addAttribute("errorTrace", errorJsonMapper.getTrace());
        model.addAttribute("timestamp", errorJsonMapper.getTimeStamp());
        model.addAttribute("status", errorJsonMapper.getStatus());
        model.addAttribute("url", request.getRequestURL());
        return "validation";
    }

    private boolean getTraceParameter(HttpServletRequest request) {
        String parameter = request.getParameter("trace");
        if (parameter == null) {
            return false;
        }
        return !"false".equalsIgnoreCase(parameter);
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest request, boolean includeStackTrace) {
        RequestAttributes requestAttributes = new ServletWebRequest(request);
        return errorAttributes.getErrorAttributes((WebRequest) requestAttributes, includeStackTrace);
    }
}
