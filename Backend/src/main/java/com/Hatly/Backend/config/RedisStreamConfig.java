package com.Hatly.Backend.config;

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
            PaymentCompletedListener paymentCompletedListener) {

        String streamKey = "payment-events";
        String groupName = "payment-group";

        try {
            redisTemplate.getConnectionFactory()
                    .getConnection()
                    .streamCommands()
                    .xGroupCreate(
                            streamKey.getBytes(),
                            groupName,
                            ReadOffset.from("0-0"),
                            true // MKSTREAM = creates stream if not exists
                    );
            log.info("Successfully initialized Redis Stream '{}' and Group '{}'", streamKey, groupName);
        } catch (Exception e) {
            log.info("Consumer group '{}' is already initialized.", groupName);
        }


        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                        .builder()
                        .pollTimeout(Duration.ofSeconds(1))
                        .build();

        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
                StreamMessageListenerContainer.create(connectionFactory, options);


        container.receive(
                Consumer.from(groupName, "payment-consumer-1"),
                StreamOffset.create(streamKey, ReadOffset.lastConsumed()),
                paymentCompletedListener
        );


        container.start();
        return container;
    }
}