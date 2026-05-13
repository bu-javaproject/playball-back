package com.playball.backend.domain.compliment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.playball.backend.domain.compliment.entity.ComplimentTagEntity;

import java.util.List;

public interface ComplimentTagRepository extends JpaRepository<ComplimentTagEntity, Long> {

    List<ComplimentTagEntity> findByComplimentId(Long complimentId);
}
