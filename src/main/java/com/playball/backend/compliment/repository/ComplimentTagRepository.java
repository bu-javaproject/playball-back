package com.playball.backend.compliment.repository;

import com.playball.backend.compliment.entity.ComplimentTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplimentTagRepository extends JpaRepository<ComplimentTagEntity, Long> {

    List<ComplimentTagEntity> findByComplimentId(Long complimentId);
}
