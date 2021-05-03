package com.example.demoredisgw.service;

import com.example.demoredisgw.repository.RedisRouteDefinitionRepository;

import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayDynamicRouteService implements ApplicationEventPublisherAware {

    private final RedisRouteDefinitionRepository redisRouteDefinitionRepository;
    ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }

    public int add(RouteDefinition route) {
        redisRouteDefinitionRepository.save(Mono.just(route)).subscribe();
        eventPublisher.publishEvent(new RefreshRoutesEvent(this));
        log.info("save and event fired....");
        return 1;
    }
    
    public int update(RouteDefinition route) {
        redisRouteDefinitionRepository.delete(Mono.just(route.getId()));
        redisRouteDefinitionRepository.save(Mono.just(route)).subscribe();
        eventPublisher.publishEvent(new RefreshRoutesEvent(this));
        return 1;
    }

    public Mono<ResponseEntity<Object>> delete(String id) {
        return redisRouteDefinitionRepository.delete(Mono.just(id))
        .then(Mono.defer(() -> Mono.just(ResponseEntity.ok().build())))
        .onErrorResume(t -> t instanceof NotFoundException, 
                        t -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }
}
