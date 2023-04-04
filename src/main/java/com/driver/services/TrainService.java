package com.driver.services;

import com.driver.EntryDto.AddTrainEntryDto;
import com.driver.EntryDto.SeatAvailabilityEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Station;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TrainService {

    @Autowired
    TrainRepository trainRepository;

    public Integer addTrain(AddTrainEntryDto trainEntryDto){

        //Add the train to the trainRepository
        //and route String logic to be taken from the Problem statement.
        //Save the train and return the trainId that is generated from the database.
        //Avoid using the lombok library
        Train train = new Train();
        train.setDepartureTime(trainEntryDto.getDepartureTime());
        train.setNoOfSeats(trainEntryDto.getNoOfSeats());
        List<Station> stationList = trainEntryDto.getStationRoute();
        String route  = "";
        for(Station s : stationList){
            route = route + s.toString()+",";
        }
        route = route.substring(0,route.length()-1);
        train.setRoute(route);
        train = trainRepository.save(train);
        return train.getTrainId();
    }

    public Integer calculateAvailableSeats(SeatAvailabilityEntryDto seatAvailabilityEntryDto){

        //Calculate the total seats available
        //Suppose the route is A B C D
        //And there are 2 seats avaialble in total in the train
        //and 2 tickets are booked from A to C and B to D.
        //The seat is available only between A to B and C to D. If a seat is empty between 2 station it will be counted to our final ans
        //even if that seat is booked post the destStation or before the boardingStation
        //Inshort : a train has totalNo of seats and there are tickets from and to different locations
        //We need to find out the available seats between the given 2 stations.
        Train train = trainRepository.findById(seatAvailabilityEntryDto.getTrainId()).get();
        String fromStation = seatAvailabilityEntryDto.getFromStation().toString();
        String toStation = seatAvailabilityEntryDto.getToStation().toString();
        Integer booked = 0;
        int total_seats = train.getNoOfSeats();
        String route = train.getRoute();
        Integer fromStationIndex = route.indexOf(fromStation);
        Integer toStationIndex = route.indexOf(toStation);
        for(Ticket t : train.getBookedTickets()){
            Integer boookedfromIndex  = route.indexOf(t.getFromStation().toString());
            Integer bookedtoIndex = route.indexOf(t.getToStation().toString());
            if(boookedfromIndex <= fromStationIndex && bookedtoIndex >= toStationIndex
               || boookedfromIndex >= fromStationIndex && boookedfromIndex < toStationIndex //without last 2 cond in if also this code works fine
               || bookedtoIndex > fromStationIndex && bookedtoIndex <= toStationIndex){
                booked += t.getPassengersList().size();
            }
        }
       return total_seats - booked;
    }

    public Integer calculatePeopleBoardingAtAStation(Integer trainId,Station station) throws Exception{

        //We need to find out the number of people who will be boarding a train from a particular station
        //if the trainId is not passing through that station
        //throw new Exception("Train is not passing from this station");
        //  in a happy case we need to find out the number of such people.
          Train train = trainRepository.findById(trainId).get();
          Boolean happycase = false;
          Integer count = 0;
          String stationS = station.toString();
          String[] route = train.getRoute().split(",");
          for(String s : route){
              if(s.equals(stationS)) happycase = true;
          }
          if(!happycase){
              throw new Exception("Train is not passing from this station");
          }
          else {
              List<Ticket> ticketList = train.getBookedTickets();
              for(Ticket t : ticketList){
                  if(t.getFromStation().toString().equals(stationS)){
                      count += t.getPassengersList().size();
                  }
              }
          }

        return count;
    }

    public Integer calculateOldestPersonTravelling(Integer trainId){

        //Throughout the journey of the train between any 2 stations
        //We need to find out the age of the oldest person that is travelling the train
        //If there are no people travelling in that train you can return 0
        Train train  = trainRepository.findById(trainId).get();
        List<Ticket> ticketList = train.getBookedTickets();
        Integer age = 0;
        for(Ticket t : ticketList){
            List<Passenger> passengers = t.getPassengersList();
            for(Passenger p : passengers){
                if(p.getAge() > age){
                    age = p.getAge();
                }
            }
        }
        return age;
    }

    public List<Integer> trainsBetweenAGivenTime(Station station, LocalTime startTime, LocalTime endTime){

        //When you are at a particular station you need to find out the number of trains that will pass through a given station
        //between a particular time frame both start time and end time included.
        //You can assume that the date change doesn't need to be done ie the travel will certainly happen with the same date (More details
        //in problem statement)
        //You can also assume the seconds and milli seconds value will be 0 in a LocalTime format.

        return null;
    }

}


