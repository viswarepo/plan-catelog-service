package com.sms.plan.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String PLAN_CREATED_TOPIC = "plan.created";

    @Bean
    public NewTopic subscriptionCreatedTopic() {
        return TopicBuilder.name(PLAN_CREATED_TOPIC)
                .partitions(3)
                .replicas(1) // single-broker local dev default - raise this for a real cluster
                .build();
    }
}
