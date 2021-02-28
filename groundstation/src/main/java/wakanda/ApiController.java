package wakanda;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {
    @GetMapping("/mapstate")
    public Map<String, MapObject> getAllMapObjects() {
        return Main.mappedObjects;
    }
}