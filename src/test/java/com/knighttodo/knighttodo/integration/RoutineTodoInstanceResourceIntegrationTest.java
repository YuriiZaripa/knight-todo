package com.knighttodo.knighttodo.integration;

import com.knighttodo.knighttodo.factories.RoutineFactory;
import com.knighttodo.knighttodo.factories.RoutineInstanceFactory;
import com.knighttodo.knighttodo.factories.RoutineTodoInstanceFactory;
import com.knighttodo.knighttodo.gateway.experience.response.ExperienceResponse;
import com.knighttodo.knighttodo.gateway.privatedb.repository.RoutineInstanceRepository;
import com.knighttodo.knighttodo.gateway.privatedb.repository.RoutineRepository;
import com.knighttodo.knighttodo.gateway.privatedb.repository.RoutineTodoInstanceRepository;
import com.knighttodo.knighttodo.gateway.privatedb.representation.Routine;
import com.knighttodo.knighttodo.gateway.privatedb.representation.RoutineInstance;
import com.knighttodo.knighttodo.gateway.privatedb.representation.RoutineTodoInstance;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.knighttodo.knighttodo.Constants.*;
import static com.knighttodo.knighttodo.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = RoutineTodoInstanceResourceIntegrationTest.DockerPostgreDataSourceInitializer.class)
@Testcontainers
public class RoutineTodoInstanceResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoutineTodoInstanceRepository routineTodoInstanceRepository;

    @Autowired
    private RoutineInstanceRepository routineInstanceRepository;

    @Autowired
    private RoutineRepository routineRepository;

    @MockBean
    private RestTemplate restTemplate;

    @AfterEach
    public void tearDown() {
        routineTodoInstanceRepository.deleteAll();
        routineInstanceRepository.deleteAll();
    }

    @Container
    public static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:11.1");

    static {
        postgresqlContainer.start();
    }

    public static class DockerPostgreDataSourceInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {

            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    applicationContext,
                    "spring.datasource.url=" + postgresqlContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgresqlContainer.getUsername(),
                    "spring.datasource.password=" + postgresqlContainer.getPassword()
            );
        }
    }

    @Test
    public void findAllRoutineInstanceTodos_shouldReturnAllRoutineTodoInstances() throws Exception {
        RoutineInstance routineInstance = routineInstanceRepository.save(RoutineInstanceFactory.routineInstance());

        routineTodoInstanceRepository.save(RoutineTodoInstanceFactory.routineTodoInstanceWithRoutineInstance(routineInstance));
        routineTodoInstanceRepository.save(RoutineTodoInstanceFactory.routineTodoInstanceWithRoutineInstance(routineInstance));

        mockMvc.perform(get(API_BASE_URL_V1 + API_BASE_ROUTINES_INSTANCES + "/" + routineInstance.getId() + API_BASE_ROUTINES_TODO_INSTANCES))
                .andExpect(status().isFound())
                .andExpect(jsonPath(buildJsonPathToLength()).value(2));
    }

    @Test
    public void findRoutineTodoInstanceById_shouldReturnExistingRoutineTodoInstance_whenIdIsCorrect() throws Exception {
        RoutineInstance routineInstance = routineInstanceRepository.save(RoutineInstanceFactory.routineInstance());

        RoutineTodoInstance savedRoutineTodoInstance = routineTodoInstanceRepository.save(RoutineTodoInstanceFactory.routineTodoInstanceWithRoutineInstance(routineInstance));

        mockMvc.perform(get(API_BASE_URL_V1 + API_BASE_ROUTINES_INSTANCES + "/" + routineInstance.getId() +
                API_BASE_ROUTINES_TODO_INSTANCES + "/" + savedRoutineTodoInstance.getId()))
                .andExpect(status().isFound())
                .andExpect(jsonPath(buildJsonPathToId()).value(savedRoutineTodoInstance.getId().toString()));
    }

    @Test
    public void findRoutineTodoInstancesByRoutineInstanceId_shouldReturnExistingRoutineTodoInstances_whenIdIsCorrect() throws Exception {
        RoutineInstance routineInstance = routineInstanceRepository.save(RoutineInstanceFactory.routineInstance());
        routineTodoInstanceRepository.save(RoutineTodoInstanceFactory.routineTodoInstanceWithRoutineInstance(routineInstance));
        routineTodoInstanceRepository.save(RoutineTodoInstanceFactory.routineTodoInstanceWithRoutineInstance(routineInstance));

        mockMvc.perform(get(API_BASE_URL_V1 + API_BASE_ROUTINES_INSTANCES + "/" + routineInstance.getId() + API_BASE_ROUTINES_TODO_INSTANCES))
                .andExpect(status().isFound())
                .andExpect(jsonPath(buildJsonPathToLength()).value(2));
    }

    @Test
    public void updateIsReady_shouldReturnOk_shouldMakeIsReadyTrue_whenRoutineTodoInstanceIdIsCorrect() throws Exception {
        Routine routine = routineRepository.save(RoutineFactory.routineInstance());
        RoutineInstance routineInstance = routineInstanceRepository.save(RoutineInstanceFactory.routineInstanceWithRoutine(routine));
        RoutineTodoInstance savedRoutineTodoInstance = routineTodoInstanceRepository.save(RoutineTodoInstanceFactory.
                routineTodoInstanceWithRoutineInstance(routineInstance));
        ExperienceResponse experienceResponse = RoutineTodoInstanceFactory.experienceResponseInstance(savedRoutineTodoInstance.getId());

        when(restTemplate.postForEntity(anyString(), any(), eq(ExperienceResponse.class)))
                .thenReturn(new ResponseEntity<>(experienceResponse, HttpStatus.OK));

        mockMvc.perform(put(API_BASE_URL_V1 + API_BASE_ROUTINES_INSTANCES + "/" + routineInstance.getId() +
                API_BASE_ROUTINES_TODO_INSTANCES + "/" + savedRoutineTodoInstance.getId() + BASE_READY)
                .param(PARAM_READY, PARAMETER_TRUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath(buildJsonPathToRoutineId()).isNotEmpty())
                .andExpect(jsonPath(buildJsonPathToExperience()).isNotEmpty())
                .andExpect(jsonPath(buildJsonPathToReadyName()).value(true));

        assertThat(routineTodoInstanceRepository.findById(savedRoutineTodoInstance.getId()).get().isReady()).isEqualTo(true);
    }

    @Test
    public void updateIsReady_shouldReturnOk_shouldMakeIsReadyFalse_whenRoutineTodoIdIsCorrect() throws Exception {
        Routine routine = routineRepository.save(RoutineFactory.routineInstance());
        RoutineInstance routineInstance = routineInstanceRepository.save(RoutineInstanceFactory.routineInstanceWithRoutine(routine));
        RoutineTodoInstance savedRoutineTodoInstanceReadyTrue = routineTodoInstanceRepository.save(RoutineTodoInstanceFactory.
                routineTodoInstanceWithRoutineReadyInstance(routineInstance));
        ExperienceResponse experienceResponse = RoutineTodoInstanceFactory.experienceResponseInstance(savedRoutineTodoInstanceReadyTrue.getId());

        when(restTemplate.postForEntity(anyString(), any(), eq(ExperienceResponse.class)))
                .thenReturn(new ResponseEntity<>(experienceResponse, HttpStatus.OK));

        mockMvc.perform(put(API_BASE_URL_V1 + API_BASE_ROUTINES_INSTANCES + "/" + routineInstance.getId() +
                API_BASE_ROUTINES_TODO_INSTANCES + "/" + savedRoutineTodoInstanceReadyTrue.getId() + BASE_READY)
                .param(PARAM_READY, PARAMETER_FALSE))
                .andExpect(status().isOk());

        assertThat(routineTodoInstanceRepository.findById(savedRoutineTodoInstanceReadyTrue.getId()).get().isReady()).isEqualTo(false);
    }
}
