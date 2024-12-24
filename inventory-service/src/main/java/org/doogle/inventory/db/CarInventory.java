//package org.doogle.inventory.db;
//
//import jakarta.annotation.PostConstruct;
//import jakarta.enterprise.context.ApplicationScoped;
//import java.util.List;
//import java.util.concurrent.CopyOnWriteArrayList;
//import java.util.concurrent.atomic.AtomicLong;
//import org.doogle.inventory.model.Car;
//
//@ApplicationScoped
//public class CarInventory {
//
//  public static final AtomicLong ids = new AtomicLong(0);
//  private List<Car> cars;
//
//  @PostConstruct
//  void initialize() {
//    cars = new CopyOnWriteArrayList<>();
//    initialData();
//  }
//
//  public List<Car> getCars() {
//    return cars;
//  }
//
//  private void initialData() {
//    Car mazda = new Car(ids.incrementAndGet(), "ABC123", "Mazda", "6");
//    cars.add(mazda);
//    Car ford = new Car(ids.incrementAndGet(), "XYZ987", "Ford", "Mustang");
//    cars.add(ford);
//  }
//}