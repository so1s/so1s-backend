package io.so1s.backend.domain.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "data_type")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DataType {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "data_type_id")
  private Long id;

  @NotBlank
  @Column(nullable = false, unique = true)
  private String name;

}
