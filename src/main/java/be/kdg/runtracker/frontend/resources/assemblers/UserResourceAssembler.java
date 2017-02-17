package be.kdg.runtracker.frontend.resources.assemblers;

import be.kdg.runtracker.backend.dom.profile.User;
import be.kdg.runtracker.frontend.controllers.UserRestController;
import be.kdg.runtracker.frontend.resources.resources.UserResource;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class UserResourceAssembler extends ResourceAssemblerSupport<User, UserResource> {

    @Autowired
    private MapperFacade mapper;

    public UserResourceAssembler() {
        super(UserRestController.class, UserResource.class);
    }

    @Override
    public UserResource toResource(User user) {
        // map Entity class to Resource class using ModelMapper Framework
        UserResource userResource = mapper.map(user, UserResource.class);

        // add HATEOAS stuff to mapped Resource
        //userResource.add(linkTo(methodOn(UserRestController.class).getUser("token", bid.getId())).withSelfRel());

        // add link to repair for which this is a bid
        //bidResource.add(linkTo(methodOn(RepairRestController.class).getRepairByRepairId(bid.getRepair().getId())).withRel("repair"));

        // add link to repairer who made this bid
        //bidResource.add(linkTo(methodOn(UserRestController.class).findUserById(bid.getRepairer().getId())).withRel("repairer"));

        // links to bids of this repair if any
        //List<Bid> bids = repair.getBids();
        //bids.forEach(bid -> repairResource.add(linkTo(methodOn(BidRestController.class).getBidById(null, bid.getId())).withRel("bids")));

        //return bidResource;

        return null;
    }
}
