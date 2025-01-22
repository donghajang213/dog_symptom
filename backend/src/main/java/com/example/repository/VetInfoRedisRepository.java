package com.example.repository;

import com.example.entity.VetInfoRedis;
import org.springframework.data.repository.CrudRepository;
import java.util.UUID;

public interface VetInfoRedisRepository extends CrudRepository<VetInfoRedis, UUID> {
}
