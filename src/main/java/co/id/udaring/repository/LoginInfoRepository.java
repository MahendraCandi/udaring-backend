package co.id.udaring.repository;

import co.id.udaring.entity.LoginInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LoginInfoRepository extends JpaRepository<LoginInfo, UUID> {
}
