package com.knighttodo.knighttodo.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knighttodo.knighttodo.factories.RoutineFactory;
import com.knighttodo.knighttodo.factories.RoutineInstanceFactory;
import com.knighttodo.knighttodo.factories.RoutineTodoFactory;
import com.knighttodo.knighttodo.factories.RoutineTodoInstanceFactory;
import com.knighttodo.knighttodo.gateway.privatedb.repository.RoutineInstanceRepository;
import com.knighttodo.knighttodo.gateway.privatedb.repository.RoutineRepository;
import com.knighttodo.knighttodo.gateway.privatedb.repository.RoutineTodoInstanceRepository;
import com.knighttodo.knighttodo.gateway.privatedb.repository.RoutineTodoRepository;
import com.knighttodo.knighttodo.gateway.privatedb.representation.Routine;
import com.knighttodo.knighttodo.gateway.privatedb.representation.RoutineInstance;
import com.knighttodo.knighttodo.gateway.privatedb.representation.RoutineTodo;
import com.knighttodo.knighttodo.gateway.privatedb.representation.RoutineTodoInstance;
import com.knighttodo.knighttodo.gateway.privatedb.representation.enums.Hardness;
import com.knighttodo.knighttodo.rest.request.RoutineInstanceRequestDto;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.knighttodo.knighttodo.Constants.API_BASE_ROUTINES;
import static com.knighttodo.knighttodo.Constants.API_BASE_ROUTINES_INSTANCES;
import static com.knighttodo.knighttodo.Constants.API_BASE_URL_V1;
import static com.knighttodo.knighttodo.TestConstants.buildGetRoutineInstanceByIdUrl;
import static com.knighttodo.knighttodo.TestConstants.buildJsonPathToHardness;
import static com.knighttodo.knighttodo.TestConstants.buildJsonPathToId;
import static com.knighttodo.knighttodo.TestConstants.buildJsonPathToLength;
import static com.knighttodo.knighttodo.TestConstants.buildJsonPathToName;
import static com.knighttodo.knighttodo.TestConstants.buildJsonPathToReadyName;
import static com.knighttodo.knighttodo.TestConstants.buildJsonPathToScariness;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(initializers = RoutineInstanceResourceIntegrationTest.DockerPostgreDataSourceInitializer.class)
@Testcontainers
public class RoutineInstanceResourceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private RoutineInstanceRepository routineInstanceRepository;

    @Autowired
    private RoutineTodoInstanceRepository routineTodoInstanceRepository;

    @Autowired
    private RoutineTodoRepository routineTodoRepository;

    @AfterEach
    public void tearDown() {
        routineTodoRepository.deleteAll();
        routineInstanceRepository.deleteAll();
    }

    @Container
    public static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:11.1").withUsername("root").withPassword("root");

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
    public void createRoutineInstance_shouldAddRoutineInstanceAndReturnIt_whenRequestIsCorrect() throws Exception {
        Routine routine = routineRepository.save(RoutineFactory.routineInstance());
        RoutineInstanceRequestDto requestDto = RoutineInstanceFactory.createRoutineInstanceRequestDto();

        mockMvc.perform(post(API_BASE_URL_V1 + API_BASE_ROUTINES + "/" + routine.getId() + API_BASE_ROUTINES_INSTANCES)
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath(buildJsonPathToName()).isNotEmpty())
                .andExpect(jsonPath(buildJsonPathToHardness()).isNotEmpty())
                .andExpect(jsonPath(buildJsonPathToScariness()).isNotEmpty())
                .andExpect(jsonPath(buildJsonPathToReadyName()).value(false))
                .andExpect(jsonPath(buildJsonPathToId()).exists());

        assertThat(routineInstanceRepository.count()).isEqualTo(1);
    }

    @Test
    public void createRoutineInstance_shouldRespondWithBadRequestStatus_whenNameIsNull() throws Exception {
        Routine routine = routineRepository.save(RoutineFactory.routineInstance());
        RoutineInstanceRequestDto requestDto = RoutineInstanceFactory.createRoutineInstanceWithNullNameValueRequestDto();

        mockMvc.perform(post(API_BASE_URL_V1 + API_BASE_ROUTINES + "/" + routine.getId() + API_BASE_ROUTINES_INSTANCES)
                .content(objectMapper.writeValueAsString(requestDto))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());

        assertThat(routineInstanceRepository.count()).isEqualTo(0);
    }

    @Test
    public void findAllRoutineInstances_shouldReturnAllTodos() throws Exception {
        Routine routine = routineRepository.save(RoutineFactory.routineInstance());
        routineInstanceRepository.save(RoutineInstanceFactory.routineInstance());
        routineInstanceRepository.save(RoutineInstanceFactory.routineInstance());

        mockMvc.perform(get(API_BASE_URL_V1 + API_BASE_ROUTINES + "/" + routine.getId() + API_BASE_ROUTINES_INSTANCES))
                .andExpect(status().isFound())
                .andExpect(jsonPath(buildJsonPathToLength()).value(2));
    }

    @Test
    public void findRoutineInstanceById_shouldReturnExistingRoutineInstance_whenIdIsCorrect() throws Exception {
        Routine routine = routineRepository.save(RoutineFactory.routineInstance());
        RoutineInstance routineInstance = routineInstanceRepository.save(RoutineInstanceFactory.routineInstanceWithRoutine(routine));

        mockMvc.perform(get(buildGetRoutineInstanceByIdUrl(routine.getId(), routineInstance.getId())))
                .andExpect(status().isFound())
                .andExpect(jsonPath(buildJsonPathToId()).value(routineInstance.getId().toString()));
    }

    @Test
    public void findRoutineInstanceById_shouldReturnWithUpdatedTodos_whenRoutineInstancesWereChangedIsCorrect() throws Exception {
        Routine routine = routineRepository.save(RoutineFactory.routineInstance());
        RoutineInstance routineInstance = routineInstanceRepository.save(RoutineInstanceFactory.routineInstanceWithRoutine(routine));

        RoutineTodo savedRoutineTodo1 = routineTodoRepository.save(RoutineTodoFactory.routineTodoWithRoutine(routine));
        RoutineTodo savedRoutineTodo2 = routineTodoRepository.save(RoutineTodoFactory.routineTodoWithRoutine(routine));

        RoutineTodoInstance savedRoutineTodoInstance1 = routineTodoInstanceRepository.save(RoutineTodoInstanceFactory
                .routineTodoInstanceWithRoutineInstanceAndRoutineTodo(routineInstance, savedRoutineTodo1));
        RoutineTodoInstance savedRoutineTodoInstance2 = routineTodoInstanceRepository.save(RoutineTodoInstanceFactory
                .routineTodoInstanceWithRoutineInstanceAndRoutineTodo(routineInstance, savedRoutineTodo2));

//        savedRoutineTodo1.getRoutineTodoInstances().add(savedRoutineTodoInstance1);
//        savedRoutineTodo2.getRoutineTodoInstances().add(savedRoutineTodoInstance2);
        routineTodoRepository.save(savedRoutineTodo1);
        routineTodoRepository.save(savedRoutineTodo2);

        savedRoutineTodo1.setRoutineTodoName("changedRoutineTodoName");
        savedRoutineTodo2.setHardness(Hardness.IMPOSSIBLE);
        routineTodoRepository.save(savedRoutineTodo1);
        routineTodoRepository.save(savedRoutineTodo2);

        mockMvc.perform(get(buildGetRoutineInstanceByIdUrl(routine.getId(), routineInstance.getId())))
                .andExpect(status().isFound())
                .andExpect(jsonPath(buildJsonPathToId()).value(routineInstance.getId().toString()))
                .andExpect(jsonPath("$.routineTodos[0].name").value(savedRoutineTodo1.getRoutineTodoName()))
                .andExpect(jsonPath("$.routineTodos[1].hardness").value(savedRoutineTodo2.getHardness()));
    }
}
