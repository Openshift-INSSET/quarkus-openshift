package fr.ccm;

import io.quarkus.runtime.util.StringUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import io.quarkus.logging.Log;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.annotations.Message;
import org.jboss.resteasy.reactive.ResponseStatus;

import java.text.MessageFormat;
import java.util.List;

@Path("/fruits")
@ApplicationScoped
public class FruitResource {

    public FruitResource(){
    }

    @GET
    @Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Fruit list(String name) {
        List<Fruit> fruits = Fruit.listAll();
        Log.info(fruits.size() + " fruits found");
        Log.info("list :" + fruits.toString());
        for(Fruit fruit : fruits) {
            if (fruit.getName().equalsIgnoreCase(name)) {
                return fruit;
            }
        }
        Log.error(MessageFormat.format("There is no fruit with the following name : {0}", name));
        throw new NotFoundException();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public List<Fruit> listAll() {
        List<Fruit> fruits = Fruit.listAll();
        Log.info(fruits.size() + " fruits found");
        Log.info("list :" + fruits.toString());
        return fruits;
    }

    @POST
    @ResponseStatus(201)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public void add(Fruit fruit) {
        List<Fruit> fruits = Fruit.listAll();
        for(Fruit f : fruits) {
            if (fruit.getName().equalsIgnoreCase(f.getName())) {
                Log.error("This fruit already exists");
                throw new NotFoundException();
            }
        }
        capitalize(fruit);
        Fruit.persist(fruit);
        Log.info("Adding a new fruit in the database");
    }

    @PUT
    @ResponseStatus(201)
    @Path("{name}")
    @Transactional
    public void put(String name, String description) {
        List<Fruit> fruits = Fruit.listAll();
        for(Fruit fruit : fruits) {
            if (fruit.getName().equalsIgnoreCase(name)) {
                Fruit.update("description = '"
                        + description
                        + "' where name = ?1", fruit.getName());
                Log.info(MessageFormat.format("{0} is successfully modified", fruit.getName()));
            }
        }
        Log.error(MessageFormat.format("There is no fruit with the following name : {0}", name));
        throw new NotFoundException();
    }

    @DELETE
    @ResponseStatus(200)
    @Path("{name}")
    @Transactional
    public void delete(String name) {
        List<Fruit> fruits = Fruit.listAll();
        for(Fruit fruit : fruits) {
            if (fruit.getName().equalsIgnoreCase(name)) {
                Fruit.delete("name", capitalize(name));
                Log.info(MessageFormat.format("{0} is successfully deleted", fruit.getName()));
            }
        }
        Log.error(MessageFormat.format("There is no fruit with the following name : {0}", name));
        throw new NotFoundException();
    }

    @DELETE
    @ResponseStatus(200)
    @Transactional
    public void deleteAll() {
        Fruit.deleteAll();
        Log.info("All fruits are successfully deleted");
    }

    private void capitalize(Fruit fruit) {
        String formattedName = fruit.getName().substring(0, 1).toUpperCase()
                + fruit.getName().substring(1).toLowerCase();
        fruit.setName(formattedName);
    }
    private String capitalize(String name) {
        return name.substring(0, 1).toUpperCase()
                + name.substring(1).toLowerCase();
    }
}