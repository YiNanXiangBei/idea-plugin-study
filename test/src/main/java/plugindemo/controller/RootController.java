package plugindemo.controller;

import org.springframework.web.bind.annotation.*;

/**
 * @author yinan
 * @date 2020/6/7
 */
@RestController
@RequestMapping(path = "/api")
public class RootController extends BaseController {
    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @PostMapping("/add")
    public String add() {
        return "add";
    }

    @RequestMapping(value = "/ttt", method = {RequestMethod.DELETE, RequestMethod.GET})
    public String ttt() {
        return "rttt";
    }

    @Override
    public String update() {
        return super.update();
    }

    @Override
    public String father() {
        return super.father();
    }
}