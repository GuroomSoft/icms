package com.guroomsoft.icms.auth.event;

import com.guroomsoft.icms.auth.dto.User;
import com.guroomsoft.icms.auth.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RegisterUserEventListener {
    @Autowired
    private UserService userService;

    @Async("DefaultAsyncTaskPool")
    @EventListener
    public void onRegisterUser(RegisterUserEvent evt)
    {
        log.info("👉 Regsiter user event listener - start");
        User user = evt.getUser();

        if (user == null)
        {
            return ;
        }

        try {
            userService.modifyPartnerInfo(user);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        log.info("👉 Register User event listener - end");
    }
}
