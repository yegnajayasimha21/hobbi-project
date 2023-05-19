package com.hobbi.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hobbi.model.entities.Hobby;
import com.hobbi.model.entities.Location;

@Repository
public interface HobbyRepository extends JpaRepository<Hobby, Long> {
    Set<Hobby> findAllByCreator(String creator);

    List<Hobby> findAllByLocation(Location location);
}