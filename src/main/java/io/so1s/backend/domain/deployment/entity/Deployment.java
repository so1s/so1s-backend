package io.so1s.backend.domain.deployment.entity;


import io.so1s.backend.domain.model.entity.ModelMetadata;
import io.so1s.backend.global.entity.BaseTimeEntity;
import io.so1s.backend.global.vo.Status;
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
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "deployment")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Deployment extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "deployment_id")
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = false)
  private String endPoint;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Status status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "model_metadata_id")
  private ModelMetadata modelMetadata;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "deployment_strategy_id")
  private DeploymentStrategy deploymentStrategy;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "resource_id")
  private Resource resource;

  public void setModelMetadata(ModelMetadata modelMetadata) {
    this.modelMetadata = modelMetadata;
    modelMetadata.getDeployments().add(this);
  }

  public void setDeploymentStrategy(DeploymentStrategy deploymentStrategy) {
    this.deploymentStrategy = deploymentStrategy;
    deploymentStrategy.getDeployments().add(this);
  }

  public void setResource(Resource resource) {
    this.resource = resource;
    resource.getDeployment().add(this);
  }

  public void updateModel(ModelMetadata modelMetadata, DeploymentStrategy deploymentStrategy,
      Resource resource) {
    this.modelMetadata = modelMetadata;
    this.deploymentStrategy = deploymentStrategy;
    this.resource = resource;
  }

  public void changeStatus(Status status) {
    this.status = status;
  }
}
