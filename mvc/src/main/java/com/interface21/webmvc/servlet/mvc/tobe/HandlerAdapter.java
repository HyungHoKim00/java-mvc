package com.interface21.webmvc.servlet.mvc.tobe;

import com.interface21.webmvc.servlet.ModelAndView;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface HandlerAdapter<T> {

    Class<T> getSupportedClass();

    ModelAndView handle(Object handler, HttpServletRequest request, HttpServletResponse response) throws Exception;
}