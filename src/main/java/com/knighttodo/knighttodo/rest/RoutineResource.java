package com.knighttodo.knighttodo.rest;

import com.knighttodo.knighttodo.domain.RoutineVO;
import com.knighttodo.knighttodo.rest.mapper.RoutineRestMapper;
import com.knighttodo.knighttodo.rest.request.RoutineRequestDto;
import com.knighttodo.knighttodo.rest.response.RoutineResponseDto;
import com.knighttodo.knighttodo.service.RoutineService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.knighttodo.knighttodo.Constants.API_BASE_ROUTINES;

@RequiredArgsConstructor
@RestController
@RequestMapping(API_BASE_ROUTINES)
@Slf4j
public class RoutineResource {

    private final RoutineService routineService;
    private final RoutineRestMapper routineRestMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Add the new Routine")
    public RoutineResponseDto addRoutine(@Valid @RequestBody RoutineRequestDto requestDto) {
        log.debug("Rest request to add routine : {}", requestDto);
        RoutineVO routineVO = routineRestMapper.toRoutineVO(requestDto);
        RoutineVO savedRoutineVO = routineService.save(routineVO);
        return routineRestMapper.toRoutineResponseDto(savedRoutineVO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.FOUND)
    @ApiOperation(value = "Find all Routines")
    public List<RoutineResponseDto> findAllRoutines() {
        log.debug("Rest request to get all routines");
        return routineService.findAll()
                .stream()
                .map(routineRestMapper::toRoutineResponseDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{routineId}")
    @ResponseStatus(HttpStatus.FOUND)
    @ApiOperation(value = "Find the Routine by id")
    public RoutineResponseDto findRoutineById(@PathVariable String routineId) {
        log.debug("Rest request to get routine by id : {}", routineId);
        RoutineVO routineVO = routineService.findById(routineId);
        return routineRestMapper.toRoutineResponseDto(routineVO);
    }

    @PutMapping("/{routineId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update the Routine by id")
    public RoutineResponseDto updateRoutine(@PathVariable String routineId,
                                                            @Valid @RequestBody RoutineRequestDto requestDto) {
        log.debug("Rest request to update routine : {}", requestDto);
        RoutineVO routineVO = routineRestMapper.toRoutineVO(requestDto);
        RoutineVO updatedRoutineVO = routineService.updateRoutine(routineId, routineVO);
        return routineRestMapper.toRoutineResponseDto(updatedRoutineVO);
    }

    @DeleteMapping("/{routineId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Delete the Routine by id")
    public void deleteRoutine(@PathVariable String routineId) {
        log.debug("Rest request to delete routine by id : {}", routineId);
        routineService.deleteById(routineId);
    }
}
