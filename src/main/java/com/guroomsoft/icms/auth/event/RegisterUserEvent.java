package com.guroomsoft.icms.auth.event;

import com.guroomsoft.icms.auth.dto.User;
import org.springframework.context.ApplicationEvent;

public class RegisterUserEvent extends ApplicationEvent {
    private User mUser;

    public RegisterUserEvent(Object source, User user)
    {
        super(source);
        this.mUser = user;
    }

    public User getUser()
    {
        return this.mUser;
    }
}
