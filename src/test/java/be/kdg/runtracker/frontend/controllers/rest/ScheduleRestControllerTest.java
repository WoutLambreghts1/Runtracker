package be.kdg.runtracker.frontend.controllers.rest;

import be.kdg.runtracker.backend.dom.scheduling.Schedule;
import be.kdg.runtracker.backend.persistence.ScheduleRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
@Transactional
public class ScheduleRestControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private ScheduleRepository scheduleRepository;
    private MockMvc mockMvc;
    private Schedule schedule1;
    private Schedule schedule2;

    @Before
    public void setup() {
        schedule1 = new Schedule("Test Schedule 1", 10, new ArrayList<>());
        schedule2 = new Schedule("Test Schedule 2", 10, new ArrayList<>());
        scheduleRepository.save(schedule1);
        scheduleRepository.save(schedule2);

        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testGetAllSchedules() throws Exception {
        mockMvc.perform(get("/api/schedules").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testNoSchedules() throws Exception {
        scheduleRepository.delete(scheduleRepository.findScheduleByName("Test Schedule 1").getSchedule_id());
        scheduleRepository.delete(scheduleRepository.findScheduleByName("Test Schedule 2").getSchedule_id());

        mockMvc.perform(get("/api/schedules").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        scheduleRepository.save(schedule1);
        scheduleRepository.save(schedule2);
    }

    @Test
    public void testGetScheduleByName() throws Exception {
        String scheduleName = "Test Schedule 1";
        mockMvc.perform(get("/api/schedules/" + scheduleName).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.name", is("Test Schedule 1")))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetScheduleWithWrongName() throws Exception {
        String scheduleName = "Non Existing Schedule";
        mockMvc.perform(get("/api/schedules/" + scheduleName).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @After
    public void removeTestSchedules() {
        if (!scheduleRepository.findAll().isEmpty()) {
            scheduleRepository.delete(scheduleRepository.findScheduleByName("Test Schedule 1").getSchedule_id());
            scheduleRepository.delete(scheduleRepository.findScheduleByName("Test Schedule 2").getSchedule_id());
        }
    }

}
