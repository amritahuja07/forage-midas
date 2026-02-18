package com.jpmc.midascore.repository;

import com.jpmc.midascore.model.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRecordRepository
        extends JpaRepository<TransactionRecord, Long> {

    // Custom queries can live here later if needed
    // e.g. List<TransactionRecord> findBySenderId(Long senderId);
}
