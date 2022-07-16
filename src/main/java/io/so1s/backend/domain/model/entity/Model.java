package io.so1s.backend.domain.model.entity;

import io.so1s.backend.global.entity.BaseTimeEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "model")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Model extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "model_id")
  private Long id;

  private String name;

  private String library;

  @OneToMany(mappedBy = "model")
  @Fetch(FetchMode.SUBSELECT)
  private List<ModelMetadata> modelMetadatas = new ArrayList<>();

}
