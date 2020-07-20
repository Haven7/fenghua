package haven.fenghua.service.impl;

import haven.fenghua.dao.UserMapper;
import haven.fenghua.model.User;
import haven.fenghua.service.UserService;
import haven.fenghua.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


/**
 * Created by CodeGenerator on 2020/07/20.
 */
@Service
@Transactional
public class UserServiceImpl extends AbstractService<User> implements UserService {
    @Resource
    private UserMapper userMapper;

}
