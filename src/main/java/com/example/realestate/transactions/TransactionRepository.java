package com.example.realestate.transactions;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

	@Query("""
			SELECT t FROM Transaction t
			WHERE (:type IS NULL OR t.type = :type)
			  AND (
			      :q IS NULL
			      OR t.city ILIKE concat('%', CAST(:q AS string), '%')
			      OR t.cityCode ILIKE concat('%', CAST(:q AS string), '%')
			  )
			ORDER BY t.time DESC
			""")
	List<Transaction> findLatestFiltered(@Param("type") TransactionType type, @Param("q") String query, org.springframework.data.domain.Pageable pageable);
}
