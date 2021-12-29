package mate.academy.service.impl;

import java.util.Optional;
import mate.academy.exception.AuthenticationException;
import mate.academy.exception.RegistrationException;
import mate.academy.lib.Inject;
import mate.academy.lib.Service;
import mate.academy.model.User;
import mate.academy.service.AuthenticationService;
import mate.academy.service.UserService;
import mate.academy.util.HashUtil;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    @Inject
    private UserService userService;

    @Override
    public User login(String email, String password) throws AuthenticationException {
        Optional<User> userFromDb = userService.findByEmail(email);
        if (userFromDb.isPresent() && checkPassword(userFromDb.get(), password)) {
            return userFromDb.get();
        }
        throw new AuthenticationException("Can't authenticate user with e-mail " + email);
    }

    @Override
    public User register(String email, String password) throws RegistrationException {
        Optional<User> checkUser = userService.findByEmail(email);
        if (checkUser.isEmpty()) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(password);
            userService.add(user);
        }
        throw new RegistrationException("User with email already exist");
    }

    private boolean checkPassword(User user, String password) {
        return user.getPassword().equals(HashUtil.hashPassword(password, user.getSalt()));
    }
}