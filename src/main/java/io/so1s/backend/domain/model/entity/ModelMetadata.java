package io.so1s.backend.domain.model.entity;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "model_metadata")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModelMetadata extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "model_metadata_id")
  private Long id;

  @Column(nullable = false)
  private String status;

  @Column(nullable = false)
  private String version;

  @Column(unique = true)
  private String fileName;

  @Column(unique = true)
  private String url;

  @Column(nullable = false)
  private String inputShape;

  @Column(nullable = false)
  private String inputDtype;

  @Column(nullable = false)
  private String outputShape;

  @Column(nullable = false)
  private String outputDtype;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "model_id")
  private Model model;

  public void setModel(Model model) {
    this.model = model;
    model.getModelMetadatas().add(this);
  }
}
