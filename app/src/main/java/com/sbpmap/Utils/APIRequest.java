package com.sbpmap.Utils;

import com.sbpmap.Map.API;

import org.apache.http.client.methods.HttpUriRequest;

/**
 * Created by JDima on 22/03/15.
 */
public class APIRequest {

        public API api;
        public String query;
        public HttpUriRequest httpUriRequest;

        public APIRequest(API api, HttpUriRequest httpUriRequest, String query) {
            this.api = api;
            this.httpUriRequest = httpUriRequest;
            this.query = query;
        }
}
