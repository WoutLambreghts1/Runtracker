package be.kdg.runtracker.frontend.controllers;

import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.backend.dom.scheduling.Schedule;
import be.kdg.runtracker.backend.persistence.ScheduleRepository;
import be.kdg.runtracker.backend.persistence.UserRepository;
import be.kdg.runtracker.frontend.util.CustomErrorType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@RepositoryRestController
@RequestMapping("/api/schedules")
public class ScheduleRestController {

    public static final Logger logger = Logger.getLogger(ScheduleRestController.class);
    private UserRepository userRepository;
    private ScheduleRepository scheduleRepository;

    @Autowired
    public ScheduleRestController(UserRepository userRepository, ScheduleRepository scheduleRepository) {
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
    }

    protected ScheduleRestController() { }

    /**
     * Find all {@link Schedule}s.
     * @return List of Schedules
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Schedule>> getAllSchedules() {
        logger.info("Fetching all schedules.");

        List<Schedule> schedules = scheduleRepository.findAll();
        if (schedules.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Schedule>>(schedules, HttpStatus.OK);
    }

    /**
     * Find {@link Schedule} by name.
     * @param userId
     * @param scheduleName
     * @return Schedule
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{scheduleId}")
    public ResponseEntity<?> getUserSchedule(@PathVariable String userId, @PathVariable String scheduleName) {
        logger.info("Fetching schedule with name: " + scheduleName + " for user with id: " + userId + ".");

        User user = userRepository.findOne(Long.valueOf(userId));
        if (user == null) {
            logger.error("User with id " + userId + " not found!");
            return new ResponseEntity<Object>(
                    new CustomErrorType("User with id " + userId + " not found!"),
                    HttpStatus.NOT_FOUND
            );
        }

        Schedule schedule = scheduleRepository.findScheduleByName(scheduleName);
        if (schedule == null) {
            logger.error("Schedule with name " + scheduleName + " not found!");
            return new ResponseEntity<Object>(
                    new CustomErrorType("Schedule with name " + scheduleName + "not found!"),
                    HttpStatus.NOT_FOUND
            );
        }
        return new ResponseEntity<Schedule>(schedule, HttpStatus.OK);
    }

}
