/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.controllers.restfuls;

import com.mcc40.crud.entities.Job;
import com.mcc40.crud.services.JobService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Yoshua
 */
@RestController
@RequestMapping("api/jobs")
public class JobRestController {

    JobService service;

    @Autowired
    public JobRestController(JobService service) {
        this.service = service;
    }

    @GetMapping("")
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.status(200).body(service.getAllJob());
    }

    @GetMapping("search")
    public String getByIdJob(String id) {
        Job job = service.getByIdJob(id);
        System.out.println(job.getTitle() + " | " + job.getMinSalary() + " | " + job.getMaxSalary());
        return "index";
    }

    @PostMapping("")
    public ResponseEntity<Map<String, String>> saveJob(@RequestBody Job job) {
        Map status = new HashMap();
        System.out.println(job);
//        if (job.getId() == null) {
//            status.put("Status", "No Content");
//            return ResponseEntity.status(204).body(status);
//        }
        String result = service.saveJob(job);
        status.put("Status", result);
        if (result.equals("Inserted") || result.equals("Updated")) {
            return ResponseEntity.accepted().body(status);
        } else {
            return ResponseEntity.status(500).body(status);
        }
    }
//

    @RequestMapping("delete")
    public String deleteJob(String id) {
        if (service.deleteJob(id)) {
            System.out.println("Delete Success");
        } else {
            System.out.println("Delete Fail");
        }
        return "index";
    }

    @RequestMapping("test-yoshua")
    public String getFirstNameLocation() {
        service.getJobTitleAndDepartmentName();
        return "index";
    }

}
