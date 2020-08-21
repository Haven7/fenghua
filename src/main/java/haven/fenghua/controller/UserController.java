package haven.fenghua.controller;

import haven.fenghua.service.UserService;
import haven.fenghua.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/user", method = RequestMethod.POST)
public class UserController {


    @Autowired
    private UserService userService;


    /**
     * 查询情报字典
     */
    @RequestMapping(value = "/qbzd", method = RequestMethod.POST)
    public Result queryQbZd( @RequestBody Map<String, Object> params) {

        return userService.queryAll(params);
    }
}
