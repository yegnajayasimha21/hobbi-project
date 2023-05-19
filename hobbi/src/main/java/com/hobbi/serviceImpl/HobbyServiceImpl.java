package com.hobbi.serviceImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hobbi.handler.NotFoundException;
import com.hobbi.model.entities.AppClient;
import com.hobbi.model.entities.BusinessOwner;
import com.hobbi.model.entities.Hobby;
import com.hobbi.model.entities.Location;
import com.hobbi.model.entities.enums.CategoryNameEnum;
import com.hobbi.model.entities.enums.LocationEnum;
import com.hobbi.repository.HobbyRepository;
import com.hobbi.service.CategoryService;
import com.hobbi.service.HobbyService;
import com.hobbi.service.LocationService;
import com.hobbi.service.UserService;

@Service
@Transactional
public class HobbyServiceImpl implements HobbyService {
    private final HobbyRepository hobbyRepository;
    @SuppressWarnings("unused")
	private final CategoryService categoryService;
    private final UserService userService;
    private final LocationService locationService;
    
    @Autowired
    public HobbyServiceImpl(HobbyRepository hobbyRepository, CategoryService categoryService, UserService userService, LocationService locationService) {
        this.hobbyRepository = hobbyRepository;
        this.categoryService = categoryService;
        this.userService = userService;
        this.locationService = locationService;
    }

    @Override
    public Hobby findHobbieById(Long id) {
        Optional<Hobby> hobby = this.hobbyRepository.findById(id);
        if (hobby.isPresent()) {
            return hobby.get();
        } else {
            throw new NotFoundException("This hobby does not exist");
        }
    }

//    @SneakyThrows
    @Override
    public void saveUpdatedHobby(Hobby hobby) throws Exception {
        Optional<Hobby> byId = this.hobbyRepository.findById(hobby.getId());
        if (byId.isPresent()) {
            deleteResourcesById(byId.get());
        }
        this.hobbyRepository.save(hobby);
    }

    @Override
    public boolean deleteHobby(long id) throws Exception {
        Optional<Hobby> byId = this.hobbyRepository.findById(id);
        if (byId.isPresent()) {
            deleteResourcesById(byId.get());
            BusinessOwner business = this.userService.findBusinessByUsername(byId.get().getCreator());
            business.getHobby_offers().remove(byId.get());
            this.userService.findAndRemoveHobbyFromClientsRecords(byId.get());
            this.hobbyRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private void deleteResourcesById(Hobby byId) throws Exception {
        String profileImgId = byId.getProfileImg_id();
        String galleryImgId1 = byId.getGalleryImg1_id();
        String galleryImgId2 = byId.getGalleryImg2_id();
        String galleryImgId3 = byId.getGalleryImg3_id();     
    }
    
    @Override
    public Set<Hobby> findHobbyMatches(String username) {
        AppClient currentUserAppClient = this.userService.findAppClientByUsername(username);
        Set<Hobby> hobby_matches = new HashSet<>();
        if (currentUserAppClient.getTestResults() != null) {
            boolean isAdded = false;
            Random rand = new Random();
            LocationEnum location = currentUserAppClient.getTestResults().getLocation();
            Location locationByName = this.locationService.getLocationByName(location);
            List<Hobby> allByLocation = this.hobbyRepository.findAllByLocation(locationByName);
            List<CategoryNameEnum> testCategoryResults = new ArrayList<>();

            testCategoryResults.add(currentUserAppClient.getTestResults().getCategoryOne());
            testCategoryResults.add(currentUserAppClient.getTestResults().getCategoryTwo());
            testCategoryResults.add(currentUserAppClient.getTestResults().getCategoryThree());
            testCategoryResults.add(currentUserAppClient.getTestResults().getCategoryFour());
            testCategoryResults.add(currentUserAppClient.getTestResults().getCategoryFive());
            testCategoryResults.add(currentUserAppClient.getTestResults().getCategorySix());

            if (allByLocation.size() > 0) {

                for (int i = 0; i < 10; i++) {
                    int randomIndex = rand.nextInt(allByLocation.size());
                    Hobby randomHobby = allByLocation.get(randomIndex);
                    if (hobby_matches.contains(randomHobby)) {
                        continue;
                    }
                    for (CategoryNameEnum testCategory : testCategoryResults) {
                        if (randomHobby.getCategory().getName().equals(testCategory)) {
                            hobby_matches.add(randomHobby);
                            isAdded = true;
                        }
                        if (isAdded) {
                            isAdded = false;
                            break;
                        }
                    }
                }
            }
        }
        return hobby_matches;
    }

    @Override
    public boolean saveHobbyForClient(Hobby hobby, String username) {
        AppClient currentUserAppClient = this.userService.findAppClientByUsername(username);
        Optional<Hobby> hobbyById = this.hobbyRepository.findById(hobby.getId());
        List<Hobby> saved_hobbies = currentUserAppClient.getSaved_hobbies();
        if (hobbyById.isPresent() && !(saved_hobbies.contains(hobbyById.get()))) {
            saved_hobbies.add(hobbyById.get());
            return true;
        }
        return false;
    }

    @Override
    public boolean removeHobbyForClient(Hobby hobby, String username) {
        AppClient currentUserAppClient = this.userService.findAppClientByUsername(username);
        Optional<Hobby> hobbyById = this.hobbyRepository.findById(hobby.getId());
        if (currentUserAppClient != null) {
            hobbyById.ifPresent(value -> currentUserAppClient.getSaved_hobbies().remove(value));
            return true;
        }
        return false;
    }

    @Override
    public boolean isHobbySaved(Long hobbyId, String username) {
        Optional<Hobby> byId = this.hobbyRepository.findById(hobbyId);
        if (byId.isPresent()) {
            AppClient currentUserAppClient = this.userService.findAppClientByUsername(username);
            return currentUserAppClient.getSaved_hobbies().contains(byId.get());
        }
        return false;
    }

    @Override
    public List<Hobby> findSavedHobbies(AppClient currentAppClient) {
        return currentAppClient.getSaved_hobbies();
    }

    @Override
    public Set<Hobby> getAllHobbiesForBusiness(String username) {
        return this.hobbyRepository.findAllByCreator(username);
    }

    @Override
    public Set<Hobby> getAllHobbieMatchesForClient(String username) {
        AppClient currentUserAppClient = this.userService.findAppClientByUsername(username);
        return currentUserAppClient.getHobby_matches();
    }

    @Override
    public void createHobby(Hobby offer) {
        this.hobbyRepository.save(offer);
    }

}
