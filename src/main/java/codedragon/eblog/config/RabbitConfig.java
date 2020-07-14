package codedragon.eblog.config;

import org.hibernate.validator.constraints.EAN;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : Code Dragon
 * create at:  2020/7/14  10:07
 */
@Configuration
public class RabbitConfig {
    public final static String ES_QUEUE = "es_queue";
    public final static String ES_EXCHANGE = "es_change";
    public final static String ES_BING_KEY = "es_change";

    @Bean
    public Queue exQueue() {
        return new Queue(ES_QUEUE);
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(ES_EXCHANGE);
    }

    @Bean
    Binding binding(Queue exQueue, DirectExchange exchange) {
        return BindingBuilder.bind(exQueue).to(exchange).with(ES_BING_KEY);
    }
}
