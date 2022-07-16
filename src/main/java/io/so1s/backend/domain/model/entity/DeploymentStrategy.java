package io.so1s.backend.domain.model.entity;

import io.so1s.backend.global.entity.BaseTimeEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "deplotment_strategy")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeploymentStrategy extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @OneToMany(mappedBy = "deploymentStrategy")
  @Fetch(FetchMode.SUBSELECT)
  private List<Deployment> deployments = new ArrayList<>();
}
