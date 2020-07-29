package com.travelplanner.travelplanner_server.restservice;

import com.travelplanner.travelplanner_server.model.Plan;
import com.travelplanner.travelplanner_server.mongodb.dal.PlanDAL;
import com.travelplanner.travelplanner_server.restservice.config.JwtTokenUtil;
import com.travelplanner.travelplanner_server.restservice.payload.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@RestController
public class PlanController {
    @Autowired
    private PlanDAL planDAL;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    @PutMapping(value = "/plan", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Plan> updatePlan(@RequestBody PlanRequest planRequest, HttpServletRequest request) {
        String token = null;
        String requestTokenHeader = request.getHeader("Authorization");
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            token = requestTokenHeader.substring(7);
        }
        String user_id = jwtTokenUtil.getUserIdFromToken(token);
        Plan plan = planDAL.findPlanByUserIdAndName(user_id, planRequest.getName());
        if (plan != null) {
            planDAL.updatePlan(plan, planRequest.getPlace_id(), new Date());
            plan.setPlace_id(planRequest.getPlace_id());
            plan.setUser_id(user_id);
            plan.setUpdatedAt(new Date());
        } else {

            plan = Plan.builder()
                    .name(planRequest.getName())
                    .place_id(planRequest.getPlace_id())
                    .updatedAt(new Date())
                    .createdAt(new Date())
                    .user_id(user_id)
                    .build();
            planDAL.createPlan(plan);
        }

        return ResponseEntity.ok().body(plan);
    }

    @DeleteMapping(value = "/plan", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deletePlan(@RequestParam(value = "name") String name, @RequestHeader(value = "Authorization") String sub) {
        String token = sub.substring(7);
        String user_id = jwtTokenUtil.getUserIdFromToken(token);

        planDAL.deletePlanByUserIdAndName(name, user_id);
    }

    @GetMapping(value = "/plans", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetPlanResponse> getPlan(HttpServletRequest request) {
        String token = null;
        String requestTokenHeader = request.getHeader("Authorization");
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            token = requestTokenHeader.substring(7);
        }
        String user_id = jwtTokenUtil.getUserIdFromToken(token);
        List<Plan> planList = planDAL.findPlansByUserId(user_id);
        GetPlanResponse getPlanResponse = new GetPlanResponse("ok", new PlansData(planList));
        return ResponseEntity.ok().body(getPlanResponse);
    }
}





