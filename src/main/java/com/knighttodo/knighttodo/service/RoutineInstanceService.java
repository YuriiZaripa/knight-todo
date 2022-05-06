package com.knighttodo.knighttodo.service;

import com.knighttodo.knighttodo.domain.RoutineInstanceVO;
import com.knighttodo.knighttodo.domain.RoutineTodoInstanceVO;
import com.knighttodo.knighttodo.domain.RoutineVO;
import com.knighttodo.knighttodo.exception.RoutineInstanceNotFoundException;
import com.knighttodo.knighttodo.gateway.RoutineInstanceGateway;
import com.knighttodo.knighttodo.gateway.RoutineTodoInstanceGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional(readOnly = true)
public class RoutineInstanceService {

    private final RoutineInstanceGateway routineInstanceGateway;
    private final RoutineService routineService;
    private final RoutineTodoInstanceGateway routineTodoInstanceGateway;

    @Transactional
    public RoutineInstanceVO save(RoutineInstanceVO routineInstanceVO, UUID routineId) {
        RoutineVO foundRoutine = routineService.findById(routineId);
        routineInstanceVO.setRoutine(foundRoutine);
        return routineInstanceGateway.save(routineInstanceVO);
    }

    public List<RoutineInstanceVO> findAll() {
        return routineInstanceGateway.findAll();
    }

    @Transactional
    public RoutineInstanceVO findById(UUID routineInstanceId) {
        RoutineInstanceVO routineInstanceVO = findRoutineInstanceVO(routineInstanceId);
        List<RoutineTodoInstanceVO> routineTodoInstances = routineTodoInstanceGateway.findByRoutineId(routineInstanceId);
        routineService.updateRoutineTodoInstances(routineInstanceVO.getRoutine().getId(), routineTodoInstances);
        return routineInstanceVO;
    }

    public RoutineInstanceVO findRoutineInstanceVO(UUID routineInstanceId) {
        return routineInstanceGateway.findById(routineInstanceId)
                .orElseThrow(() -> {
                    log.error(String.format("Routine Instance with such id:%s can't be " + "found", routineInstanceId));
                    return new RoutineInstanceNotFoundException(
                            String.format("Routine Instance with such id:%s can't be " + "found", routineInstanceId));
                });
    }

    @Transactional
    public RoutineInstanceVO update(UUID routineInstanceId, RoutineInstanceVO changedRoutineInstanceVO) {
        RoutineInstanceVO routineInstanceVO = findById(routineInstanceId);
        routineInstanceVO.setName(changedRoutineInstanceVO.getName());
        routineInstanceVO.setHardness(changedRoutineInstanceVO.getHardness());
        routineInstanceVO.setScariness(changedRoutineInstanceVO.getScariness());
        routineInstanceVO.setReady(changedRoutineInstanceVO.isReady());
        return routineInstanceGateway.save(routineInstanceVO);
    }

    @Transactional
    public void deleteById(UUID routineInstanceId) {
        routineInstanceGateway.deleteAllRoutineTodoInstancesByRoutineInstanceId(routineInstanceId);
        routineInstanceGateway.deleteById(routineInstanceId);
    }
}
