package com.example.backend.user.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.user.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

  List<Post> findByUserId(Long userId);

  Page<Post> findByUser_Id(Long userId, Pageable pageable);

}

/*
 * save()
 * findById()
 * findAll()
 * delete()
 * existsById()
 */
