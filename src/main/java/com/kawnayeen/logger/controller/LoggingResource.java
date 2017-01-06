package com.kawnayeen.logger.controller;

import com.kawnayeen.logger.model.LoggerUser;
import com.kawnayeen.logger.model.entity.Account;
import com.kawnayeen.logger.model.entity.Application;
import com.kawnayeen.logger.model.entity.Log;
import com.kawnayeen.logger.model.housekeeping.DisplayName;
import com.kawnayeen.logger.model.housekeeping.LogInfo;
import com.kawnayeen.logger.security.annotation.BasicAuthentication;
import com.kawnayeen.logger.security.annotation.CurrentUser;
import com.kawnayeen.logger.security.annotation.TokenAuthentication;
import com.kawnayeen.logger.security.token.auth.JwtUtil;
import com.kawnayeen.logger.service.AccountService;
import com.kawnayeen.logger.service.ApplicationService;
import com.kawnayeen.logger.service.LogService;
import com.kawnayeen.logger.service.StringUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

/**
 * Created by kawnayeen on 1/2/17.
 */
@RestController
public class LoggingResource {

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    StringUtility stringUtility;
    @Autowired
    AccountService accountService;
    @Autowired
    ApplicationService applicationService;
    @Autowired
    LogService logService;

    @BasicAuthentication
    @RequestMapping(
            value = "/auth",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String,String>> helloWorld(@CurrentUser LoggerUser loggerUser) {
        String accessToken = jwtUtil.generateToken(loggerUser);
        return new ResponseEntity<>(Collections.singletonMap("accessToken",accessToken), HttpStatus.OK);
    }

    @TokenAuthentication
    @RequestMapping(
            value = "/register",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Application> createNewApplication(@CurrentUser LoggerUser loggerUser, @RequestBody DisplayName displayName) {
        Account account = accountService.findOne(loggerUser.getId());
        Application application = new Application();
        application.setDisplayName(displayName.getDisplayName());
        application.setApplicationId(stringUtility.randomString());
        application.setApplicationSecret(stringUtility.randomString());
        application.setAccount(account);
        try {
            application = applicationService.create(application);
            return new ResponseEntity<>(application, HttpStatus.CREATED);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @TokenAuthentication
    @RequestMapping(
            value = "/log",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Map<String, Boolean>> addNewLog(@CurrentUser LoggerUser loggerUser, @RequestBody LogInfo logInfo) {
        Log log = new Log();
        log.setApplication(applicationService.findByApplicationId(logInfo.getApplicationId()));
        log.setLogger(logInfo.getLogger());
        log.setLevel(logInfo.getLogLevel());
        log.setMessage(logInfo.getMessage());
        try {
            logService.create(log);
            return new ResponseEntity<>(Collections.singletonMap("success", true), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @TokenAuthentication
    @RequestMapping(
            value = "/profile",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Account> getAccountDetails(@CurrentUser LoggerUser loggerUser){
        return new ResponseEntity<>(accountService.findOne(loggerUser.getId()),HttpStatus.OK);
    }
}
