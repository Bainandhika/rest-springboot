package com.baghaskara.kafka_redis_demo.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// @Component: Mendaftarkan class ini ke Spring Context (IoC Container)
@Component
public class UserEventConsumer {

    /**
     * @KafkaListener: The magic annotation.
     * Spring akan otomatis membuat thread pool, manage consumer group, handle offset commit,
     * dan melakukan retry jika terjadi error. 
     * 
     * topics: Nama topic yang mau di-listen.
     * groupId: Consumer Group ID. Jika ada multiple instances aplikasi ini, 
     *          Kafka akan membagi-bagi partition ke instance yang berbeda (Load Balancing).
     */
    @KafkaListener(topics = "user-events", groupId = "baghaskara-user-group")
    public void consumeUserEvent(String message) {
        // Di production, biasanya kita parse JSON string ke Object di sini.
        System.out.println("🔥 [KAFKA CONSUMER] Received event: " + message);
        
        // Lu bisa taruh logic bisnis di sini, misal:
        // - Kirim email welcome
        // - Sinkronisasi data ke Data Warehouse
        // - Trigger notifikasi ke Slack
    }
}