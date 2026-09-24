package com.sms.plan.event;

import com.sms.plan.config.KafkaTopicConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
  */
@Component
@Slf4j
public class PlanEventPublisher {

    private final KafkaTemplate<String, PlanCreatedEvent> kafkaTemplate;

    public PlanEventPublisher(KafkaTemplate<String, PlanCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishPlanCreated(PlanCreatedEvent event) {
        // Keyed by organizationId so all events for one tenant land on the
        // same partition, preserving per-tenant ordering if that ever matters.
        kafkaTemplate.send(KafkaTopicConfig.PLAN_CREATED_TOPIC, event.organizationId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish PlanCreatedEvent for plan {}: {}",
                                event.planId(),
                                ex.getMessage(),
                                ex);
                    } else {
                        log.info("Published PlanCreatedEvent {} for create new plan {} (partition={}, offset={})",
                                event.eventId(),
                                event.planId(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
