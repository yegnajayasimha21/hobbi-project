package com.hobbi.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.hobbi.model.dto.AppClientSignUpDto;
import com.hobbi.model.dto.BusinessRegisterDto;
import com.hobbi.model.dto.UpdateAppClientDto;
import com.hobbi.model.dto.UpdateBusinessDto;
import com.hobbi.model.entities.AppClient;
import com.hobbi.model.entities.BusinessOwner;
import com.hobbi.model.entities.UserEntity;
import com.hobbi.model.entities.UserRoleEntity;
import com.hobbi.model.entities.enums.GenderEnum;
import com.hobbi.model.entities.enums.UserRoleEnum;
import com.hobbi.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest extends AbstractTest {
    @Autowired
    private UserController controller;
    private BusinessRegisterDto businessRegisterDto;
    private UpdateAppClientDto updateAppClientDto;
    private UpdateBusinessDto updateBusinessDto;
    private AppClientSignUpDto appClientSignUpDto;
    private BusinessOwner businessOwner;
    private AppClient appClient;

    @MockBean
    private UserService userService;

    @BeforeEach
    public void setUp() {
        ModelMapper modelMapper = new ModelMapper();
        super.setUp();

        // prepare data client
        appClientSignUpDto = new AppClientSignUpDto();
        appClientSignUpDto.setUsername("user");
        appClientSignUpDto.setPassword("topsecret");
        appClientSignUpDto.setEmail("testemail@gmail.com");
        appClientSignUpDto.setFullName("full name");
        appClientSignUpDto.setGender(GenderEnum.FEMALE);
        UserRoleEntity roleUser = new UserRoleEntity();
        roleUser.setRole(UserRoleEnum.USER);
        UserRoleEntity roleAdmin = new UserRoleEntity();
        roleAdmin.setRole(UserRoleEnum.ADMIN);
        appClient = modelMapper.map(appClientSignUpDto, AppClient.class);
        appClient.setRoles(List.of(roleUser, roleAdmin));

        //update client
        updateAppClientDto = new UpdateAppClientDto();
        updateAppClientDto.setId((1L));
        updateAppClientDto.setPassword("topsecret");
        updateAppClientDto.setFullName("full name");
        updateAppClientDto.setGender(GenderEnum.FEMALE);


        // prepare data business
        businessRegisterDto = new BusinessRegisterDto();
        businessRegisterDto.setUsername("business");
        businessRegisterDto.setPassword("topsecret");
        businessRegisterDto.setEmail("test@gmail.com");
        businessRegisterDto.setBusinessName("business name");
        businessRegisterDto.setAddress("Business address");
        businessOwner = modelMapper.map(businessRegisterDto, BusinessOwner.class);
        businessOwner.setRoles(List.of(roleUser, roleAdmin));


        //update business
        updateBusinessDto = new UpdateBusinessDto();
        updateBusinessDto.setId(1L);
        updateBusinessDto.setBusinessName("Business Name");
        updateBusinessDto.setPassword("password");
        updateBusinessDto.setBusinessName("Bizz name");

        //prepare data user
        UserEntity user = new UserEntity();
        user.setRoles(List.of(roleUser, roleAdmin));

    }

    @Test
    public void contextLoads() {
        assertThat(controller).isNotNull();
    }

    @Test
    public void signup_should_work() throws Exception {
        String uri = "/signup";

        String inputJson = super.mapToJson(appClientSignUpDto);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
    }

    @Test
    public void register_business_should_work() throws Exception {
        String uri = "/register";

        String inputJson = super.mapToJson(businessRegisterDto);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
    }

    @Test
    public void update_user_should_work() throws Exception {
        String uri = "/user";

        String inputJson = super.mapToJson(updateAppClientDto);
        appClient.setId(updateAppClientDto.getId());

        when(userService.findAppClientById(1L)).thenReturn(appClient);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
    }

    @Test
    public void update_business_should_work() throws Exception {
        String uri = "/business";

        String inputJson = super.mapToJson(updateBusinessDto);
        businessOwner.setId(updateBusinessDto.getId());

        when(userService.findBusinessOwnerById(1L)).thenReturn(businessOwner);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(201, status);
    }

    @Test
    public void delete_user_should_work_when_not_found() throws Exception {
        String uri = "/user/1";
        Long id = 1L;

        when(userService.deleteUser(id)).thenReturn(false);

        String inputJson = super.mapToJson(id);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(404, status);
    }

    @Test
    public void delete_user_should_work() throws Exception {
        String uri = "/user/1";
        Long id = 1L;

        when(userService.deleteUser(id)).thenReturn(true);

        String inputJson = super.mapToJson(id);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(200, status);
    }
}
