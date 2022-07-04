package io.so1s.backend.domain.model.entity;

import io.so1s.backend.global.entity.BaseTimeEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModelMetadata extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Builder.Default
  private String url = "";

  @Builder.Default
  private String version = "";

  @Builder.Default
  @Column(columnDefinition = "TEXT")
  private String info = "";

  @ManyToOne
  @Nullable
  private Model model;
}
