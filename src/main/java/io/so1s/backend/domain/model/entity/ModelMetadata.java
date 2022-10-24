package io.so1s.backend.domain.model.entity;

import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.global.entity.BaseTimeEntity;
import io.so1s.backend.global.vo.Status;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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
  @Enumerated(EnumType.STRING)
  private Status status;

  @Column(nullable = false)
  private String version;

  @Column(nullable = false)
  private String fileName;

  @Column(nullable = false)
  private String url;

  @Column(nullable = false)
  private String inputShape;

  @Column(nullable = false)
  private String inputDtype;

  @Column(nullable = false)
  private String outputShape;

  @Column(nullable = false)
  private String outputDtype;

  @Column(nullable = false)
  private String deviceType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "model_id")
  private Model model;

  @Builder.Default
  @OneToMany(mappedBy = "deploymentStrategy")
  @Fetch(FetchMode.SUBSELECT)
  private List<Deployment> deployments = new ArrayList<>();

  public void setModel(Model model) {
    this.model = model;
    model.getModelMetadatas().add(this);
  }

  public void changeStatus(Status status) {
    this.status = status;
  }
}
