package id.my.jvm.postgresredisonk8s.controller;

import id.my.jvm.postgresredisonk8s.model.Employee;
import id.my.jvm.postgresredisonk8s.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-postgres-redis-on-k8s-sample
 * User: hendisantika
 * Link: s.id/hendisantika
 * Email: hendisantika@yahoo.co.id
 * Telegram : @hendisantika34
 * Date: 22/07/26
 * Time: 16.05
 * To change this template use File | Settings | File Templates.
 */

@Log4j2
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    @GetMapping(value = "")
    @Cacheable(cacheNames = "employee_list")
    public List<Employee> findAll(){
        log.info("fetch to db");
        return employeeRepository.findAll();
    }

    @PostMapping(value = "")
    @CacheEvict(cacheNames = "employee_list", allEntries = true)
    public Employee saveEmployee(@RequestBody Employee employee){
        return employeeRepository.save(employee);
    }
}
