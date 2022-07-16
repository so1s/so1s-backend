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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "deployment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Deployment extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "deployment_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "model_metadata_id")
  private ModelMetadata modelMetadata;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "deployment_strategy_id")
  private DeploymentStrategy deploymentStrategy;

  public void setModelMetadata(ModelMetadata modelMetadata) {
    this.modelMetadata = modelMetadata;
    modelMetadata.getDeployments().add(this);
  }

  public void setDeploymentStrategy(DeploymentStrategy deploymentStrategy) {
    this.deploymentStrategy = deploymentStrategy;
    deploymentStrategy.getDeployments().add(this);
  }
}
