package app.costumes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class CostumesController {

    private List<Costume> receivedCostumes = new ArrayList<Costume>();

    @Value("${subscriber.url}")
    private String subscriberUrl;

    @RequestMapping(value = "/costume", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public void AddCostume(@RequestBody Costume costume) {
        receivedCostumes.add(costume);
    }


    @RequestMapping(value = "/costume/{id}", method = RequestMethod.GET)
    public ResponseEntity getCostume(@PathVariable int id) {
        if (receivedCostumes.stream().filter(x -> x.getCostumeId() == id).count() > 0)
            return new ResponseEntity(HttpStatus.OK);
        else
            return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/sale/{id}", method = RequestMethod.POST)
    public ResponseEntity AddCostume(@PathVariable int id) {

        try {
            HttpClient client = HttpClient.newBuilder().build();

            String body = "{\"costumeId\":\"" + id + "\", \"channel\":\"Amazon\"}";
            String url = subscriberUrl + "/notify/sale";
            log.info("Sending "+body+" to url "+url);
            var request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header("Content-Type", "application/json")
                    .uri(URI.create(url))
                    .build();
            client.send(request, HttpResponse.BodyHandlers.discarding());
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
