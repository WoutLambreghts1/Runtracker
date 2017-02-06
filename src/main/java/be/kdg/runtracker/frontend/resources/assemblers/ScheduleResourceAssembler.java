package be.kdg.runtracker.frontend.resources.assemblers;

import be.kdg.runtracker.backend.dom.scheduling.Schedule;
import be.kdg.runtracker.frontend.controllers.ScheduleRestController;
import be.kdg.runtracker.frontend.controllers.UserRestController;
import be.kdg.runtracker.frontend.resources.ScheduleResource;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class ScheduleResourceAssembler extends ResourceAssemblerSupport<Schedule, ScheduleResource> {

    @Autowired
    private MapperFacade mapper;

    public ScheduleResourceAssembler() {
        super(ScheduleRestController.class, ScheduleResource.class);
    }

    @Override
    public List<ScheduleResource> toResources(Iterable<? extends Schedule> entities) {
        return super.toResources(entities);
    }

    @Override
    public ScheduleResource toResource(Schedule schedule) {
        // map Entity class to Resource class using ModelMapper Framework
        //ScheduleResource scheduleResource = mapper.map(schedule, ScheduleResource.class);

        // add HATEOAS stuff to mapped Resource
        //scheduleResource.add(linkTo(methodOn(ScheduleRestController.class).getUserSchedule(null, bid.getId())).withSelfRel());

        // add link to repair for which this is a bid
        //scheduleResource.add(linkTo(methodOn(RepairRestController.class).getRepairByRepairId(bid.getRepair().getId())).withRel("repair"));

        // add link to repairer who made this bid
        //scheduleResource.add(linkTo(methodOn(UserRestController.class).findUserById(bid.getRepairer().getId())).withRel("repairer"));

        return null;
    }
}
