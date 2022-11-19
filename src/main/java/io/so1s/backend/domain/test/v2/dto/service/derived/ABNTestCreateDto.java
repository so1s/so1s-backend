package io.so1s.backend.domain.test.v2.dto.service.derived;

import io.so1s.backend.domain.test.v2.dto.service.base.ABNTestBaseDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ABNTestCreateDto extends ABNTestBaseDto {

}
