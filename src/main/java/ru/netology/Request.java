package ru.netology;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URLEncodedUtils;
import org.apache.hc.core5.net.WWWFormCodec;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class Request {
    private String method;
    private String path;
    private List<String> headers;
    private String body;
    private List<NameValuePair> queryParams;
    private List<NameValuePair> xWWWFormEncodedParams;
    private Map<String,String> multipartFormDataParams;


    public void setMethod(String method) {
        this.method = method;
    }

    public void setPath(String path) {
        if (path.contains("?")) {
            this.path = path.substring(0, path.indexOf('?'));
            queryParams = URLEncodedUtils.parse(path.substring(path.indexOf('?')+1),StandardCharsets.UTF_8);
        } else {
            this.path = path;
        }
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public List<NameValuePair> getQueryParams() {
        return queryParams;
    }

    public List<NameValuePair> getxWWWFormEncodedParams() {
        return xWWWFormEncodedParams;
    }

    public void setxWWWFormEncodedParams() {
        this.xWWWFormEncodedParams = WWWFormCodec.parse(body,StandardCharsets.UTF_8);
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getMultipartFormDataParams() {
        return multipartFormDataParams;
    }

    public void setMultipartFormDataParams() {
        // Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();
// Configure a repository (to ensure a secure temp location is used)
        File repository = new File("temp");
        factory.setRepository(repository);

// Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);
// Parse the request
    }
}
