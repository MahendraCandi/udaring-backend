package co.id.udaring.repository;

import co.id.udaring.entity.RefreshTokenInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RefreshTokenInfoRepository extends JpaRepository<RefreshTokenInfo, UUID> {
}
