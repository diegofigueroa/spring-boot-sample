package demo.service;

import demo.domain.model.User;

public interface UserService {

    User save(User user);

    User findById(Long id);

    Iterable<User> findAll();
}
