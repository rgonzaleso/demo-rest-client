package com.rgonzaleso.restclient.repository;

import com.rgonzaleso.restclient.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<Request, Long> {
}
