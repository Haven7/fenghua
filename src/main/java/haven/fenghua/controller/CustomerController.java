package haven.fenghua.controller;

import haven.fenghua.service.CustomerService;
import haven.fenghua.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/customer", method = RequestMethod.POST)
public class CustomerController {


    @Autowired
    private CustomerService customerService;


    /**
     * 查询顾客信息
     */
    @RequestMapping(value = "/queryCustomer", method = RequestMethod.POST)
    public Result queryQbZd( @RequestBody Map<String, Object> params) {

        return customerService.queryAll(params);
    }
}
