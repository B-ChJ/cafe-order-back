package com.sparta.cafeorderback.repository;

import com.sparta.cafeorderback.entity.Menu;
import jakarta.persistence.LockModeType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface MenuRepository extends JpaRepository<Menu, Long> {

  @Lock(LockModeType.PESSIMISTIC_READ)
  @Query("select m from Menu m where m.id in :ids")
  List<Menu> findAllByIdWithPessimisticLock(List<Long> ids);
}
