package demo.web.controller;

import demo.domain.model.User;
import demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.*;

@RestController
@RequestMapping(value = "/v1/users")
public class UsersController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = GET)
    //@Transactional(readOnly = true)
    public Iterable index() {
        return userService.findAll();
    }

    @RequestMapping(value = "{id}", method = GET)
    public User show(@PathVariable Long id) {
        return userService.findById(id);
    }

    @RequestMapping(method = POST)
    public ResponseEntity<?> create(@RequestBody @Valid final User user) {
        final User result = userService.save(user);
        final HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setLocation(fromCurrentRequest().
                path("/{id}").
                buildAndExpand(result.getId())
                .toUri());

        return new ResponseEntity<>(result, httpHeaders, CREATED);
    }

}
