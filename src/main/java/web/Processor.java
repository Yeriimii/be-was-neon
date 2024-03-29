package web;

import http.HttpRequest;
import http.HttpResponse;

public interface Processor {
    void process(HttpRequest request, HttpResponse response);
}
