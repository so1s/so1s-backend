package io.so1s.backend.domain.test.v2.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.transaction.Transactional;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "abn_test")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ABNTest extends BaseTimeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "abn_test_id")
  private Long id;

  @NotBlank
  @Column(unique = true)
  @Size(min = 3, max = 100)
  private String name;


  @JsonIgnore
  @ManyToMany(fetch = FetchType.LAZY)
  @NotNull
  @JoinTable(
      name = "abn_tests_to_elements",
      joinColumns = {@JoinColumn(name = "abn_test_id")},
      inverseJoinColumns = {@JoinColumn(name = "abn_test_element_id")}
  )
  @NotNull
  // Hide from builder
  // https://stackoverflow.com/a/39920328/11853111
  private final List<ABNTestElement> elements = new ArrayList<>();

  @NotBlank
  private String domain;

  @NotBlank
  private String endPoint;

  @Transactional
  public void addElement(ABNTestElement element) {
    elements.add(element);
  }

  @Transactional
  public void addElements(List<ABNTestElement> appendix) {
    elements.addAll(appendix);
  }
}
