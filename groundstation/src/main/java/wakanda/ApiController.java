package wakanda;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {
    @GetMapping("/mapstate")
    public Collection<MapObject> getAllMapObjects() {
        return Main.mappedObjects.values();
    }
}
