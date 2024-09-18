package com.mmry.until;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * @author : 明明如月
 * @date : 2024/5/4 13:32
 */
@Component
public class NonStaticResourceHttpRequestHandler  extends ResourceHttpRequestHandler {
    public final static String ATTR_FILE = "NON-STATIC-FILE";

    @Override
    protected Resource getResource(HttpServletRequest request) {
        String filePath =  (String) request.getAttribute(ATTR_FILE);
        return new FileSystemResource(filePath);
    }
}
