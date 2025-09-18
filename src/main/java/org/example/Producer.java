package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class Producer {

    private static final String TOPIC_NAME = "file-lines-topic";
    private static final String BOOTSTRAP_SERVERS = "localhost:9092"; // Endereço do seu broker Kafka

    public static void main(String[] args) {
        // Configurações do Produtor Kafka
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        // Criar o produtor Kafka
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(properties)) {

            // Usar try-with-resources para garantir que o arquivo seja fechado
            try (BufferedReader br = new BufferedReader(new FileReader("C:\\Projects\\KafkaPubSub\\data.txt"))) {
                String line;
                int lineNumber = 0;
                while ((line = br.readLine()) != null) {
                    // Criar um registro com a linha do arquivo
                    ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, line);

                    // Enviar o registro para o Kafka
                    producer.send(record, (metadata, exception) -> {
                        if (exception == null) {
                            System.out.printf("Linha enviada com sucesso para o tópico %s, partição %d, offset %d%n",
                                    metadata.topic(), metadata.partition(), metadata.offset());
                        } else {
                            System.err.println("Erro ao enviar a linha: " + exception.getMessage());
                        }
                    });
                    lineNumber++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}