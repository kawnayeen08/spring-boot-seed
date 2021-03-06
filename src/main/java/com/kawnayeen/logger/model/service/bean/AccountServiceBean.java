package com.kawnayeen.logger.model.service.bean;

import com.kawnayeen.logger.model.entity.Account;
import com.kawnayeen.logger.model.service.AccountService;
import com.kawnayeen.logger.model.repository.AccountRepository;
import com.kawnayeen.logger.model.service.exception.AccountNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class AccountServiceBean implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Account findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    @Override
    public Account findOne(Long id) {
        Account account = accountRepository.findOne(id);
        if(account==null)
            throw new AccountNotFoundException();
        return account;
    }
}
