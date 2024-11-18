package org.example.quan_ao_f4k.repository.order;

import org.example.quan_ao_f4k.model.order.ShippingInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShippingInfoRepository extends JpaRepository<ShippingInfo, Long>, JpaSpecificationExecutor<ShippingInfo> {
    // for shopping site
    @Query("SELECT s from ShippingInfo s where s.status = 1 and s.user.id = :userId order by s.isDefault desc ")
    List<ShippingInfo> findAllByUserId(@Param("userId") Long id);

    @Query("SELECT s from ShippingInfo s where s.status = 1 and s.user.id = :userId and s.isDefault = true")
    ShippingInfo findDefaultByUserId(@Param("userId") Long id);
}
