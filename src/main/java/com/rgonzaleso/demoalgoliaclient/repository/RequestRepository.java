package com.rgonzaleso.demoalgoliaclient.repository;

import com.rgonzaleso.demoalgoliaclient.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepository extends JpaRepository<Request, Long> {
}
