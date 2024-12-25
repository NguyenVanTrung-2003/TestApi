package com.example.demo.userrepo;

import com.example.demo.model.user;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface userrepo extends JpaRepository <user,Long> {
    List<user> findByIdIn(List<Long> ids);

    List<user> findAllByOrderByNameAsc();

    List<user> findAllByOrderByNameDesc();

    List<user> findByName(String name);

    List<user> findByDc(String dc);

    List<user> findByAge(int age);

    List<user> findByNameStartingWith(String prefix);

    @Query("select u from user u where lower(u.name) like lower(concat('%', :name, '%'))")
    List<user> findByNameContainingIgnoreCase(@Param("name") String name);
}
