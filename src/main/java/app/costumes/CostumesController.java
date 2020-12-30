package app.costumes;

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
public class CostumesController {

    private List<Costume> receivedCostumes = new ArrayList<Costume>();


    @RequestMapping(value = "/costume", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public void AddCostume(@RequestBody Costume costume){
        receivedCostumes.add(costume);
    }


    @RequestMapping(value = "/costume/{id}", method = RequestMethod.GET )
    public ResponseEntity getCostume(@PathVariable int id){
         if(receivedCostumes.stream().filter(x -> x.getCostumeId() == id).count()>0)
             return new ResponseEntity(HttpStatus.OK);
         else
             return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/sale/{id}", method = RequestMethod.POST)
    public void AddCostume(@PathVariable int id) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().build();

        var request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString("{\"costumeId\":\"" + id + "\", \"channel\":\"Amazon\"}"))
                .header("Content-Type", "application/json")
                .uri(URI.create("http://localhost:8082/notify/sale"))
                .build();

        client.send(request, HttpResponse.BodyHandlers.discarding());
    }
}
