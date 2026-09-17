package com.example.backend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.user.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

}

/*
 * save()
 * findById()
 * findAll()
 * delete()
 * existsById()
 */
