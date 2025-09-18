package org.example;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Consumer {
    private static final String TOPIC_NAME = "file-lines-topic";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092"; // Endereço do seu broker Kafka
    private static final String GROUP_ID = "line-processing-group";
    private static final int NUM_THREADS = 5;

    // Usar uma lista thread-safe para armazenar os resultados
    private static final List<String> processedLines = Collections.synchronizedList(new ArrayList<>());
    public static void main(String[] args) throws InterruptedException {
        // Configurações do Consumidor Kafka
        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        for (int i = 0; i < NUM_THREADS; i++) {
            executor.submit(() -> {
                // Cada thread cria seu próprio consumidor
                try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties)) {
                    consumer.subscribe(Collections.singletonList(TOPIC_NAME));

                    System.out.println("Consumidor " + Thread.currentThread().getId() + " iniciado.");

                    while (true) {
                        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                        records.forEach(record -> {
                            String processedLine = record.value().toUpperCase();
                            processedLines.add(processedLine);
                            System.out.println("Thread " + Thread.currentThread().getId() + " processou: " + processedLine);
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        // Adicionando um hook para desligar o executor graciosamente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Desligando o executor...");
            executor.shutdown();
            try {
                executor.awaitTermination(60, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

        // Adicione um delay para a demonstração
        Thread.sleep(10000);
        System.out.println("Processamento concluído. Linhas processadas: " + processedLines.size());
        executor.shutdownNow(); // Força o desligamento
    }
}
