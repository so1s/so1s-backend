package io.so1s.backend.domain.model.entity;

import io.so1s.backend.domain.deployment.entity.Deployment;
import io.so1s.backend.global.entity.BaseTimeEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
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

  private String url;

  private String version;

  private String info;

  private String status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "model_id")
  private Model model;

  @OneToMany(mappedBy = "modelMetadata")
  @Fetch(FetchMode.SUBSELECT)
  private List<Deployment> deployments = new ArrayList<>();

  public void setModel(Model model) {
    this.model = model;
    model.getModelMetadatas().add(this);
  }
}
