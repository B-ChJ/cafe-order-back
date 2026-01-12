package com.sparta.cafeorderback.repository;

import com.sparta.cafeorderback.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {

}
