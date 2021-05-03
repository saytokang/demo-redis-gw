package com.example.demoredisgw.web;

import com.example.demoredisgw.service.GatewayDynamicRouteService;

import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/gw")
@RequiredArgsConstructor
public class GatewayController {
    private final GatewayDynamicRouteService gatewayDynamicRouteService;   

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody RouteDefinition route) {
        log.info("param : {}", route);
        int rs = gatewayDynamicRouteService.add(route);
        return ResponseEntity.ok().body("rs: " + rs);
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody RouteDefinition route) {
        int rs = gatewayDynamicRouteService.update(route);
        return ResponseEntity.ok().body("rs: " + rs);
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> delete(String id) {

        return gatewayDynamicRouteService.delete(id);
    }
}
