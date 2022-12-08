package ru.netology;

import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URLEncodedUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class Request {
    private String method;
    private String path;
    private List<String> headers;
    private List<NameValuePair> queryParams;


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
}
