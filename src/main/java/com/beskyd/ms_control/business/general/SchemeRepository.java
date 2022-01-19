package com.beskyd.ms_control.business.general;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface SchemeRepository extends JpaRepository<Scheme, String>{

}
