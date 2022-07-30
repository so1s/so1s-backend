package io.so1s.backend.domain.deployment.entity;

import java.util.ArrayList;
import java.util.List;
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
@Table(name = "deplotment_strategy")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeploymentStrategy {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @Builder.Default
  @OneToMany(mappedBy = "deploymentStrategy")
  @Fetch(FetchMode.SUBSELECT)
  private List<Deployment> deployments = new ArrayList<>();
}
