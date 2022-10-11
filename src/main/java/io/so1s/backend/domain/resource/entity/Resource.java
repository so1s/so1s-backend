package io.so1s.backend.domain.resource.entity;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "resource")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Resource extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "resource_id")
  private Long id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = false)
  private String cpu;

  @Column(nullable = false)
  private String memory;

  private String gpu;

  @Column(nullable = false)
  private String cpuLimit;

  @Column(nullable = false)
  private String memoryLimit;

  private String gpuLimit;

  @Builder.Default
  @Fetch(FetchMode.SUBSELECT)
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "resource")
  private List<Deployment> deployment = new ArrayList<>();
}
