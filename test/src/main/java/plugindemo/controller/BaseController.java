package plugindemo.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yinan
 * @date 2020/6/10
 */
@RestController
@RequestMapping("/api2")
public class BaseController extends FatherController {

    @PostMapping("/create")
    public String create() {
        return "create";
    }

    @PostMapping("/update")
    public String update() {
        return "update";
    }

    @Override
    public String father() {
        return super.father();
    }
}
