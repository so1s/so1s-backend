package io.so1s.backend.domain.model.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Builder
@Getter
@Table(name = "library")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Library {

  @Id
  @Column(name = "library_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  private String name;

  @Builder.Default
  @OneToMany(mappedBy = "library")
  @Fetch(FetchMode.SUBSELECT)
  private List<Model> models = new ArrayList<>();
}
