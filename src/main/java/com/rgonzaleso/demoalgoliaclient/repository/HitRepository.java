package com.rgonzaleso.demoalgoliaclient.repository;

import com.rgonzaleso.demoalgoliaclient.entity.Hit;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query(
            value = """
    SELECT *
        FROM hit
        WHERE
            hidden = false
            AND (cast(:author as varchar) IS NULL OR author = :author)
            AND (cast(:tags as varchar) IS NULL OR data @> cast(:tags as jsonb))
            AND (cast(:title as varchar) IS NULL OR title like %:title%)
            AND (cast(:month as varchar) IS NULL OR month = :month)
             
    """,
            countQuery = """
    SELECT COUNT(*)
        FROM hit
        WHERE
            hidden = false
            AND (cast(:author as varchar) IS NULL OR author = :author)
            AND (cast(:tags as varchar) IS NULL OR data @> cast(:tags as jsonb))
            AND (cast(:title as varchar) IS NULL OR title like %:title%)
            AND (cast(:month as varchar) IS NULL OR month = :month)
    """,
            nativeQuery = true
    )
    Page<Hit> findByAuthorTagsTitleMonth(
            @Param("author") String author,
            @Param("tags") String tags,
            @Param("title") String title,
            @Param("month") String month,
            Pageable pageable
    );

    @Modifying
    @Transactional
    @Query("""
        UPDATE Hit h
        SET h.hidden = true
        WHERE h.id IN :ids
    """)
    int hideByIds(@Param("ids") List<Long> ids);

    @Query("SELECT MAX(h.storyId) FROM Hit h")
    Long findMaxStoryId();
}
