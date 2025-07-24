package com.kstore.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class RouteController {

    @Autowired
    private RouteLocator routeLocator;

    @GetMapping("/gateway/routes")
    public Flux<String> getRoutes() {
        return routeLocator.getRoutes()
                .map(route -> "Route ID: " + route.getId() + 
                             ", URI: " + route.getUri());
    }

    @GetMapping("/gateway/info")
    public String getGatewayInfo() {
        return "Gateway Routes Available:\n" +
               "1. Service Discovery: /k-store-user-service/api/users/register\n" +
               "2. StripPrefix Route: /user/api/users/register\n" +
               "3. Direct Route: /users/register\n" +
               "4. API Route: /api/users/register\n\n" +
               "Example: POST /user/api/users/register";
    }
}
