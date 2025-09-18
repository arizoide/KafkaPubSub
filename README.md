Pré-requisitos

Java JDK (versão 11 ou superior)

Apache Maven (para gerenciar as dependências)

Docker e Docker Compose (para rodar o Kafka e o Zookeeper facilmente)



Como Executar



Siga os passos abaixo para configurar e rodar o projeto.

Passo 1: Configurar o Ambiente Kafka
Crie um arquivo docker-compose.yml e adicione a configuração para o Kafka e Zookeeper. Você pode encontrar exemplos completos na documentação do Docker ou em tutoriais.

Inicie os serviços do Docker:

Bash

docker-compose up -d

Crie o tópico do Kafka. O nome do container pode variar, use docker ps para encontrá-lo.

Bash

docker exec -it <nome_do_container_kafka> kafka-topics.sh --bootstrap-server localhost:9092 --topic file-lines-topic --create --partitions 1 --replication-factor 1

Crie o arquivo data.txt na raiz do projeto com o conteúdo que você deseja processar.

Execute a classe FileProducer para que ela envie as linhas do arquivo para o Kafka.

Execute a classe LineProcessor para que ela comece a consumir as mensagens do Kafka e processá-las.



