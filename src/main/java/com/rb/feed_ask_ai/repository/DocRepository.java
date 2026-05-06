package com.rb.feed_ask_ai.repository;

import com.rb.feed_ask_ai.entity.DocumentEntity;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocRepository extends JpaRepository<DocumentEntity, Long> {
    Optional<Object> findByDocumentName(@Nullable String originalFilename);
}
