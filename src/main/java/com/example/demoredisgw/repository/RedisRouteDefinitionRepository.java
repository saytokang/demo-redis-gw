package com.example.demoredisgw.repository;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.json.JsonParseException;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class  RedisRouteDefinitionRepository implements RouteDefinitionRepository {

    private final StringRedisTemplate redisTemplate;
    private final String KEY = "gw.routes";

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        List<RouteDefinition> routes = new ArrayList<>();
        redisTemplate.opsForHash().values(KEY).stream()
        .forEach(r -> {
            RouteDefinition convertRoute = toRoute(r);
            if (convertRoute != null) {
                routes.add(convertRoute);
            }  
        });
        return Flux.fromIterable(routes);
    }

    private RouteDefinition toRoute(Object r) {
        String json = (String) r;
        ObjectMapper mapper = new ObjectMapper();
        try {
           return  mapper.readValue(json, RouteDefinition.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } 
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return route.flatMap(r -> {
            String json = toJSON(r);
            log.info("json: {}", json);

            if (json != null) {
                redisTemplate.opsForHash().put(KEY, r.getId(), toJSON(r));
                log.info("redis save OK !!!!");
                return Mono.empty();
            } else {
                log.error("redis save FAIL !!!!"); 
                return Mono.defer(() -> Mono.error(new JsonParseException()));
            }            
        });
    }

    private String toJSON(RouteDefinition r) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(r);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return routeId.flatMap(r -> {
            if (redisTemplate.opsForHash().hasKey(KEY, routeId)) {
                redisTemplate.opsForHash().delete(KEY, routeId);
                return Mono.empty();
            } else {
                return Mono.defer(() -> Mono.error(new NotFoundException(routeId + " is not found.")));
            }
            
        });
    }
    
}
