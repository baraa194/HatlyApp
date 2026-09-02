package com.Hatly.Backend.config;

import com.Hatly.Backend.order.service.OrderStatusStreamListener;
import com.Hatly.Backend.payment.service.PaymentCompletedListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.time.Duration;

@Configuration
@Slf4j
public class RedisStreamConfig {

    @Bean
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamContainer(
            RedisConnectionFactory connectionFactory,
            StringRedisTemplate redisTemplate,
            PaymentCompletedListener paymentCompletedListener,
            OrderStatusStreamListener orderStatusStreamListener) {

        createGroupIfNotExists(redisTemplate, "payment-events", "payment-group");
        createGroupIfNotExists(redisTemplate, "order-status-stream", "order-group");

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                        .builder()
                        .pollTimeout(Duration.ofSeconds(2))
                        .build();

        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
                StreamMessageListenerContainer.create(connectionFactory, options);

        // مهم: استخدم ReadOffset.latest()
        container.receive(
                Consumer.from("payment-group", "payment-consumer-1"),
                StreamOffset.create("payment-events", ReadOffset.from(">")),
                paymentCompletedListener
        );

        container.receive(
                Consumer.from("order-group", "order-consumer-1"),
                StreamOffset.create("order-status-stream", ReadOffset.from(">")),
                orderStatusStreamListener
        );
        container.start();
        log.info("Redis Stream listeners started successfully");
        return container;
    }

    private void createGroupIfNotExists(StringRedisTemplate redisTemplate, String streamKey, String groupName) {
        try {
            redisTemplate.opsForStream().createGroup(streamKey, ReadOffset.from("0-0"), groupName);
            log.info("Created stream group: {} on {}", groupName, streamKey);
        } catch (Exception e) {
            log.info("Group {} already exists on {}", groupName, streamKey);
        }
    }
}