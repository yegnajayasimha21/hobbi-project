package com.hobbi.model.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.hobbi.model.entities.enums.GenderEnum;

@Entity
@Table(name = "app_clients")
public class AppClient extends UserEntity implements Serializable {
    private String fullName;
    private GenderEnum gender;
    private Test testResults;
    private Set<Hobby> hobby_matches;
    private List<Hobby> saved_hobbies;

    public AppClient() {
    }

    public AppClient(String username, String email, List<UserRoleEntity> roles, String password, String fullName, GenderEnum gender) {
        super(username, email, roles, password);
        this.fullName = fullName;
        this.gender = gender;
    }

    @Column(name = "full_name", nullable = false)
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public GenderEnum getGender() {
        return gender;
    }

    public void setGender(GenderEnum gender) {
        this.gender = gender;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    public Set<Hobby> getHobby_matches() {
        return hobby_matches;
    }

    public void setHobby_matches(Set<Hobby> hobby_matches) {
        this.hobby_matches = hobby_matches;
    }

    @OneToOne(cascade = CascadeType.REMOVE)
    public Test getTestResults() {
        return testResults;
    }

    public void setTestResults(Test testResults) {
        this.testResults = testResults;
    }

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    public List<Hobby> getSaved_hobbies() {
        return saved_hobbies;
    }

    public void setSaved_hobbies(List<Hobby> saved_hobbies) {
        this.saved_hobbies = saved_hobbies;
    }
}
