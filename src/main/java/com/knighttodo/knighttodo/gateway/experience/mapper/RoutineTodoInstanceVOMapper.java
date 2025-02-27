package com.knighttodo.knighttodo.gateway.experience.mapper;

import com.knighttodo.knighttodo.domain.RoutineTodoInstanceVO;
import com.knighttodo.knighttodo.gateway.experience.request.ExperienceRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoutineTodoInstanceVOMapper {

    @Mapping(target = "todoId", source = "id")
    @Mapping(target = "userId", ignore = true)
    ExperienceRequest routineTodoInstanceToExperienceRequest(RoutineTodoInstanceVO routineTodoInstanceVO);
}
