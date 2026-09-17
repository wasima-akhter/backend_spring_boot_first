
package com.example.backend.user.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @Column(unique = true, nullable = false)
  private String email;

  private String password;

  @OneToMany(mappedBy = "user")
  private List<Post> posts = new ArrayList<>();

  public User() {
  }

  public User(String name, String email, String password) {
    this.name = name;
    this.email = email;
    this.password = password;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}

/*
 * @OneToMany(mappedBy = "user")
 *
 * means:
 *
 * "The relationship is already controlled by the user field inside Post."
 *
 * In other words:
 *
 * User
 * │
 * │ @OneToMany
 * │ mappedBy = "user"
 * ↓
 * Post.user
 * │
 * │ @ManyToOne
 * │ @JoinColumn
 * ↓
 * posts.user_id
 */
