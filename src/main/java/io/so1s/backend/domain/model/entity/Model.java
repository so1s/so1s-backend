package io.so1s.backend.domain.model.entity;

import io.so1s.backend.global.entity.BaseTimeEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Model extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Builder.Default
  @Column(unique = true)
  private String name = "";

  @OneToMany(mappedBy = "model", fetch = FetchType.LAZY, orphanRemoval = true)
  private List<ModelMetadata> metadatas = new ArrayList<>();

}
