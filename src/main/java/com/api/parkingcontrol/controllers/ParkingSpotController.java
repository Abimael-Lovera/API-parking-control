package com.api.parkingcontrol.controllers;

import com.api.parkingcontrol.dtos.ParkingSpotDto;
import com.api.parkingcontrol.models.ParkingSpotModel;
import com.api.parkingcontrol.services.ParkingSpotService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/parking-spot")
public class ParkingSpotController {

    final ParkingSpotService parkingSpotService;

    public ParkingSpotController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }

    @PostMapping
    public ResponseEntity<Object> saveParkingSpot (@RequestBody @Valid ParkingSpotDto parkingSpotDto){

        if(parkingSpotService.existsByLicensePlateCar(parkingSpotDto.getLicensePlateCar())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: License Plate Car is already in use!");
        }
        if(parkingSpotService.existsByParkingSpotNumber(parkingSpotDto.getParkingSpotNumber())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot is already in use!");
        }

        var parkingSpotModel = new ParkingSpotModel();
        BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel); //converter os dados validados para o modelo
        parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC"))); // para inserir dados que nao sao validados
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel));
    }

    @GetMapping
    public ResponseEntity<List<ParkingSpotModel>> getAllParkingSpots(){
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getOneParkingSpot(@PathVariable(value = "id") UUID id){
        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
        if (parkingSpotModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(parkingSpotModelOptional.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found.");
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteParkingSpot(@PathVariable(value = "id") UUID id){
        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
        if (parkingSpotModelOptional.isPresent()) {
            parkingSpotService.delete(parkingSpotModelOptional.get());
            return ResponseEntity.status(HttpStatus.OK).body("Parking Spot delete successfully");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found.");
    }
// Metodo Largo
//    @PutMapping("/{id}")
//    public ResponseEntity<Object> updateParkingSpot(@PathVariable(value = "id") UUID id,
//                                                    @RequestBody @Valid ParkingSpotDto parkingSpotDto){
//        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
//        if (!parkingSpotModelOptional.isPresent()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found. Put");
//        }
//        var parkingSpotModel = parkingSpotModelOptional.get();
//        parkingSpotModel.setParkingSpotNumber(parkingSpotDto.getParkingSpotNumber());
//        parkingSpotModel.setModelCar(parkingSpotDto.getModelCar());
//        parkingSpotModel.setBrandCar(parkingSpotDto.getBrandCar());
//        parkingSpotModel.setColorCar(parkingSpotDto.getColorCar());
//        parkingSpotModel.setResponsibleName(parkingSpotDto.getResponsibleName());
//        parkingSpotModel.setApartament(parkingSpotDto.getApartament());
//        parkingSpotModel.setBlock(parkingSpotDto.getBlock());
//
//        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.save(parkingSpotModel));
//    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateParkingSpot(@PathVariable(value = "id") UUID id,
                                                    @RequestBody @Valid ParkingSpotDto parkingSpotDto){
        Optional<ParkingSpotModel> parkingSpotModelOptional = parkingSpotService.findById(id);
        if (!parkingSpotModelOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found.");
        }
        var parkingSpotModel = new ParkingSpotModel();
        BeanUtils.copyProperties(parkingSpotDto, parkingSpotModel);
        parkingSpotModel.setId(parkingSpotModelOptional.get().getId());
        parkingSpotModel.setRegistrationDate(parkingSpotModelOptional.get().getRegistrationDate());
        return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.save(parkingSpotModel));
    }
}