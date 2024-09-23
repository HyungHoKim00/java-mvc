package com.techcourse;

import com.interface21.webmvc.servlet.ModelAndView;
import com.interface21.webmvc.servlet.mvc.tobe.AnnotationHandlerAdapter;
import com.interface21.webmvc.servlet.mvc.tobe.AnnotationHandlerMapping;
import com.interface21.webmvc.servlet.mvc.tobe.HandlerAdapter;
import com.interface21.webmvc.servlet.mvc.tobe.HandlerAdapters;
import com.interface21.webmvc.servlet.mvc.tobe.HandlerMapping;
import com.interface21.webmvc.servlet.mvc.tobe.HandlerMappings;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Set;
import java.util.stream.Collectors;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DispatcherServlet extends HttpServlet {

    private static final String CONTROLLER_PACKAGE = "com.techcourse.controller";
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(DispatcherServlet.class);

    private HandlerMappings handlerMappings;
    private HandlerAdapters handlerAdapters;

    public DispatcherServlet() {
    }

    @Override
    public void init() {
        Set<HandlerMapping> mappings = getCustomMappings();
        mappings.add(new AnnotationHandlerMapping(CONTROLLER_PACKAGE));
        handlerMappings = new HandlerMappings(mappings);
        handlerAdapters = new HandlerAdapters(
                new ControllerHandlerAdapter(),
                new AnnotationHandlerAdapter()
        );
    }

    private Set<HandlerMapping> getCustomMappings() {
        Reflections reflections = new Reflections("com.techcourse");
        return reflections.getSubTypesOf(HandlerMapping.class)
                .stream()
                .map(DispatcherServlet::createInstance)
                .collect(Collectors.toSet());
    }

    private static HandlerMapping createInstance(Class<? extends HandlerMapping> mappingClass) {
        try {
            return mappingClass.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void service(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException {
        final String requestURI = request.getRequestURI();
        log.debug("Method : {}, Request URI : {}", request.getMethod(), requestURI);

        try {
            final Object handler = handlerMappings.getHandler(request);
            final HandlerAdapter<?> adapter = handlerAdapters.getAdapter(handler);
            final ModelAndView modelAndView = adapter.handle(handler, request, response);
            modelAndView.renderView(request, response);
        } catch (Throwable e) {
            log.error("Exception : {}", e.getMessage(), e);
            throw new ServletException(e.getMessage());
        }
    }
}
