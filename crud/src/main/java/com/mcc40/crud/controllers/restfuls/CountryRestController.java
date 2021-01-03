/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcc40.crud.controllers.restfuls;

import com.mcc40.crud.entities.Country;
import com.mcc40.crud.services.CountryService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author asus
 */
@RestController
@RequestMapping("api/countries")
public class CountryRestController {
    
    CountryService service;
    
    @Autowired
    public CountryRestController(CountryService service){
        this.service = service;
    }
    
    //Get All
    @GetMapping("")
    public ResponseEntity< List<Map<String, Object>>> getAllCountry() {
        
        List<Country> countries = service.getAllCountries();
        
        List<Map<String, Object>> mapList = new ArrayList<>();
        
        for (Country country : countries) {
            Map map = new LinkedHashMap();
            
            map.put("id", country.getId());
            map.put("name", country.getName());
            map.put("regionId", country.getRegion().getId());
            map.put("regionName", country.getRegion().getName());
            
            mapList.add(map);
        }
        
        return ResponseEntity.status(200).body(mapList);
    }

    //Get method search by keyword
    @GetMapping("search")
    public ResponseEntity<List<Map<String, Object>>> getCountry(String keyword){
        List<Country> countries = service.getAllCountries();
        List<Country> result = (List<Country>) countries
                .stream()
                .filter((country) ->
                        country.getId().contains(keyword) ||
                        country.getName().contains(keyword) ||
                        country.getRegion().toString().contains(keyword)
                    ).collect(Collectors.toList());
        
        List<Map<String, Object>> mapList = new ArrayList<>();
        
        for (Country country : result) {
            Map map = new LinkedHashMap();
            
            mapList.add(map);
        }
        
        return ResponseEntity.status(200).body(mapList);
    }
    
    @PostMapping("")
    public ResponseEntity<Map<String, String>> saveCountry(@RequestBody Country country){
        Map status = new HashMap();
        System.out.println(country);
        if (country.getId() == null) {
            status.put("Status", "No Content");
            return ResponseEntity.status(200).body(status);
        }
        String result = service.saveCountry(country);
        status.put("Status", result);
        if (result.equals("Inserted") || result.equals("Updated")) {
            return ResponseEntity.accepted().body(status);
        }
        return ResponseEntity.status(500).body(status);
    }
    
    //delete by id
    @DeleteMapping("delete")
    public String deleteCountry(String id){
        Country country = new Country();
        country = service.getByIdCountry(id);
        if(country.getId().equals(id)){
            service.deleteById(id);
            return "Delete Success";
        }
        return "Delete Failed";
    }
}
