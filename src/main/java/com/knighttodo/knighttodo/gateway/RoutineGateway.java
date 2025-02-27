package com.knighttodo.knighttodo.gateway;

import com.knighttodo.knighttodo.domain.RoutineVO;
import com.knighttodo.knighttodo.gateway.privatedb.mapper.RoutineMapper;
import com.knighttodo.knighttodo.gateway.privatedb.repository.RoutineRepository;
import com.knighttodo.knighttodo.gateway.privatedb.representation.Routine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class RoutineGateway {

    private final RoutineRepository routineRepository;
    private final RoutineMapper routineMapper;

    public RoutineVO save(RoutineVO routineVO) {
        Routine savedRoutine = routineRepository.save(routineMapper.toRoutine(routineVO));
        return routineMapper.toRoutineVO(savedRoutine);
    }

    public List<RoutineVO> findAll() {
        return routineRepository.findAll().stream().map(routineMapper::toRoutineVO).collect(Collectors.toList());
    }

    public Optional<RoutineVO> findById(UUID routineId) {
        return routineRepository.findById(routineId).map(routineMapper::toRoutineVO);
    }

    public void deleteById(UUID routineId) {
        routineRepository.deleteById(routineId);
    }

    public void deleteAllRoutineInstancesByRoutineId(UUID routineId) {
        routineRepository.deleteAllRoutineInstancesByRoutineId(routineId);
    }

    public void deleteAllRoutineTodosByRoutineId(UUID routineId) {
        routineRepository.deleteAllRoutineTodosByRoutineId(routineId);
    }
}
