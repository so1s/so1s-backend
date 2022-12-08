package io.so1s.backend.domain.test.v2.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.so1s.backend.domain.test.v2.entity.ABNTest;
import io.so1s.backend.domain.test.v2.entity.QABNTest;
import io.so1s.backend.domain.test.v2.entity.QABNTestElement;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ABNTestQueryRepositoryImpl implements
    ABNTestQueryRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public List<ABNTest> findAllByDeploymentId(Long deploymentId) {
    return jpaQueryFactory.selectFrom(QABNTest.aBNTest)
        .innerJoin(QABNTest.aBNTest.elements, QABNTestElement.aBNTestElement)
        .where(QABNTestElement.aBNTestElement.deployment.id.eq(deploymentId))
        .fetch();
  }
}
