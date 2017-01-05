package com.kawnayeen.logger.service;

import com.kawnayeen.logger.model.entity.Log;
import com.kawnayeen.logger.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by kawnayeen on 1/5/17.
 */
@Service
public class LogServiceBean implements LogService{
    @Autowired
    private LogRepository logRepository;

    @Override
    public Log create(Log log) {
        if(log.getId()!=null)
            return null;
        return logRepository.save(log);
    }
}
