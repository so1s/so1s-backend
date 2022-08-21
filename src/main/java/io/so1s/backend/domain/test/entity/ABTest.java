package io.so1s.backend.domain.test.entity;


import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.global.entity.BaseTimeEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "test")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ABTest extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "test_id")
  private Long id;

  @NotBlank
  @Column(unique = true)
  @Size(min = 3, max = 100)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "a_id")
  @NotNull
  private Deployment a;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "b_id")
  @NotNull
  private Deployment b;

  @NotBlank
  private String domain;


  public void update(Deployment a, Deployment b, String domain) {
    this.a = a;
    this.b = b;
    this.domain = domain;
  }
}
