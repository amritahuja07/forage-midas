package com.jpmc.midascore;

import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.model.Incentive;
import com.jpmc.midascore.model.TransactionRecord;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.entity.UserRecord;

import java.math.BigDecimal;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Bean;




@Component
public class TransactionKafkaListener {

    @Value("${general.kafka-topic}")
    private String topicName;

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;

    private final RestTemplate restTemplate = new RestTemplate();


    // 🔌 Constructor injection (Spring wiring)
    public TransactionKafkaListener(UserRepository userRepository,
                                    TransactionRecordRepository transactionRecordRepository) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }

    @KafkaListener(topics = "${general.kafka-topic}",
            groupId = "midas-transaction-consumer")
    public void listen(Transaction transaction) {
        System.out.println("🔥 LISTENER FIRED 🔥");
        System.out.println("Received transaction: " + transaction);

        // 1. Extract data from Kafka message
        Long senderId = transaction.getSenderId();
        Long recipientId = transaction.getRecipientId();
        BigDecimal amount = BigDecimal.valueOf(transaction.getAmount());

        // 2. Load users from DB
        Optional<UserRecord> senderOpt = userRepository.findById(senderId);
        Optional<UserRecord> recipientOpt = userRepository.findById(recipientId);

        // 3. Validate users exist
        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) {
            System.out.println("Invalid transaction: sender or recipient not found");
            return; // discard transaction
        }

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();

        // 4. Validate balance
        if (sender.getBalance() < amount.floatValue()) {
            System.out.println("Invalid transaction: insufficient balance");
            return; // discard transaction
        }

        // If we reach here → transaction is VALID
        System.out.println("Transaction is valid");

        // Call Incentive API
        String incentiveUrl = "http://localhost:8080/incentive";
        Incentive incentive = restTemplate.postForObject(incentiveUrl, transaction, Incentive.class);

        double incentiveAmount = 0.0;
        if (incentive != null) {
            incentiveAmount = incentive.getAmount();
        }


        // 5. Create transaction record
        TransactionRecord record = new TransactionRecord(sender, recipient, amount, BigDecimal.valueOf(incentiveAmount)
        );


        // 6. Save transaction
        transactionRecordRepository.save(record);

        // 7. Update balances
        sender.setBalance(sender.getBalance() - amount.floatValue());
        recipient.setBalance(recipient.getBalance() + amount.floatValue() + (float) incentiveAmount);


        // 8. Persist updated users
        userRepository.save(sender);
        userRepository.save(recipient);

        if ("wilbur".equalsIgnoreCase(sender.getName()) || "wilbur".equalsIgnoreCase(recipient.getName())) {
            System.out.println("💰 WILBUR FINAL STATE");
            System.out.println("Sender: " + sender.getName() + " | Balance: " + sender.getBalance());
            System.out.println("Recipient: " + recipient.getName() + " | Balance: " + recipient.getBalance());
        }

        System.out.println("Transaction persisted and balances updated");

    }

}
