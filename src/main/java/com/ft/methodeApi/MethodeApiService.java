package com.ft.methodeApi;

import com.ft.methodeApi.healthcheck.MethodeHealthCheck;
import com.ft.methodeApi.service.ContentResource;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

public class MethodeApiService extends Service<MethodeApiConfiguation> {

    public static void main(String[] args) throws Exception {
        new MethodeApiService().run(args);
    }

    @Override
    public void initialize(Bootstrap<MethodeApiConfiguation> bootstrap) {
    }

    @Override
    public void run(MethodeApiConfiguation configuration, Environment environment) throws Exception {
        environment.addResource(new ContentResource());

		environment.addHealthCheck(new MethodeHealthCheck("preditor-methode-01.svc.ft.com", "9092", "automate1", "automate1"));
    }
}
