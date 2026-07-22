package id.my.jvm.postgresredisonk8s.repository;

import id.my.jvm.postgresredisonk8s.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by IntelliJ IDEA.
 * Project : spring-boot-postgres-redis-on-k8s-sample
 * User: hendisantika
 * Link: s.id/hendisantika
 * Email: hendisantika@yahoo.co.id
 * Telegram : @hendisantika34
 * Date: 22/07/26
 * Time: 16.04
 * To change this template use File | Settings | File Templates.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
