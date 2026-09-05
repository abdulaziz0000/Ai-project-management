package com.project_management.repository;

import com.project_management.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    List<Comment> findByTaskId(UUID taskId);

    void deleteByProjectId(@Param("projectId") UUID projectId);

    @Query("""
            SELECT c
            FROM Comment c
            WHERE c.task.project.id = :projectId
            AND c.createdAt >= :from
            AND c.createdAt <= :to
            ORDER BY c.createdAt ASC
            """)
    List<Comment> findProjectCommentsBetween(
            @Param("projectId") UUID projectId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );
}