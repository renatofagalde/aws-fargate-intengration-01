package br.com.likwi.awsfargate.service;

import br.com.likwi.awsfargate.model.File;
import br.com.likwi.awsfargate.model.SNSMessage;
import br.com.likwi.awsfargate.repository.FileRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
@Slf4j
public class Consumer {

    private ObjectMapper objectMapper;
    private FileRepository fileRepository;
    private AmazonS3 amazonS3;

    public Consumer(ObjectMapper objectMapper, FileRepository fileRepository, AmazonS3 amazonS3) {
        this.objectMapper = objectMapper;
        this.fileRepository = fileRepository;
        this.amazonS3 = amazonS3;
    }


    @JmsListener(destination = "${aws.sqs.queue.file.events.name}")
    public void receiveProductEvent(TextMessage textMessage) throws JMSException, IOException {

        final SNSMessage snsMessage = this.objectMapper.readValue(textMessage.getText(), SNSMessage.class);
        final S3EventNotification s3EventNotification = this.objectMapper
                .readValue(snsMessage.getMessage(), S3EventNotification.class);

        processS3Notification(s3EventNotification);


    }

    @SneakyThrows
    private void processS3Notification(S3EventNotification s3EventNotification) {

        for (S3EventNotification.S3EventNotificationRecord notification : s3EventNotification.getRecords()) {
            final S3EventNotification.S3Entity s3 = notification.getS3();
            final String name = s3.getBucket().getName();
            final String key = s3.getObject().getKey();

            String fileObject = download(name, key);
            final File file = this.objectMapper.readValue(fileObject, File.class);
            log.info("Arquivo recebido " + file.getFileNumber());

            this.fileRepository.save(file);
            log.info("Arquivo salvo");

            this.amazonS3.deleteObject(name, key);
        }
    }

    private String download(String name, String key) throws IOException {
        final S3Object object = this.amazonS3.getObject(name, key);

        StringBuilder stringBuilder = new StringBuilder();
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(object.getObjectContent()));
        String content = null;
        while ((content = bufferedReader.readLine()) != null) {
            stringBuilder.append(content);
        }
        return stringBuilder.toString();

    }

}
