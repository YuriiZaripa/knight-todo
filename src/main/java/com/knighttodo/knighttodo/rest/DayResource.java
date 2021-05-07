package com.knighttodo.knighttodo.rest;

import com.knighttodo.knighttodo.domain.DayVO;
import com.knighttodo.knighttodo.rest.mapper.DayRestMapper;
import com.knighttodo.knighttodo.rest.request.DayRequestDto;
import com.knighttodo.knighttodo.rest.response.DayResponseDto;
import com.knighttodo.knighttodo.service.DayService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.knighttodo.knighttodo.Constants.API_BASE_DAYS;

@RequiredArgsConstructor
@RestController
@RequestMapping(API_BASE_DAYS)
public class DayResource {

    private final DayService dayService;
    private final DayRestMapper dayRestMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Add the new Day")
    public DayResponseDto addDay(@Valid @RequestBody DayRequestDto requestDto) {
        DayVO dayVO = dayRestMapper.toDayVO(requestDto);
        DayVO savedDayVO = dayService.save(dayVO);
        return dayRestMapper.toDayResponseDto(savedDayVO);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.FOUND)
    @ApiOperation(value = "Find all Days")
    public List<DayResponseDto> findAllDays() {
        return dayService.findAll()
                .stream()
                .map(dayRestMapper::toDayResponseDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{dayId}")
    @ResponseStatus(HttpStatus.FOUND)
    @ApiOperation(value = "find the Day by id")
    public DayResponseDto findDayById(@PathVariable String dayId) {
        DayVO dayVO = dayService.findById(dayId);
        return dayRestMapper.toDayResponseDto(dayVO);
    }

    @PutMapping("/{dayId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Update the Day by id")
    public DayResponseDto updateDay(@PathVariable String dayId, @Valid @RequestBody DayRequestDto requestDto) {
        DayVO dayVO = dayRestMapper.toDayVO(requestDto);
        DayVO updatedDayVO = dayService.updateDay(dayId, dayVO);
        return dayRestMapper.toDayResponseDto(updatedDayVO);
    }

    @DeleteMapping("/{dayId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Delete the Day by id")
    public void deleteDay(@PathVariable String dayId) {
        dayService.deleteById(dayId);
    }
}
