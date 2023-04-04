package com.driver.services;


import com.driver.EntryDto.BookTicketEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.PassengerRepository;
import com.driver.repository.TicketRepository;
import com.driver.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TicketService {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    TrainRepository trainRepository;

    @Autowired
    PassengerRepository passengerRepository;


    public Integer bookTicket(BookTicketEntryDto bookTicketEntryDto)throws Exception{

        //Check for validity
        //Use bookedTickets List from the TrainRepository to get bookings done against that train
        // Incase the there are insufficient tickets
        // throw new Exception("Less tickets are available");
        //otherwise book the ticket, calculate the price and other details
        //Save the information in corresponding DB Tables
        //Fare System : Check problem statement
        //Incase the train doesn't pass through the requested stations
        //throw new Exception("Invalid stations");
        //Save the bookedTickets in the train Object
        //Also in the passenger Entity change the attribute bookedTickets by using the attribute bookingPersonId.
       //And the end return the ticketId that has come from db

       Train train = trainRepository.findById(bookTicketEntryDto.getTrainId()).get();
        String fromStation = bookTicketEntryDto.getFromStation().toString();
        String toStation = bookTicketEntryDto.getToStation().toString();
        Integer booked = 0;

        String route = train.getRoute();
        Integer fromStationIndex = route.indexOf(fromStation);
        Integer toStationIndex = route.indexOf(toStation);
        if(fromStationIndex == -1 || toStationIndex == -1){
            throw new Exception("Invalid stations");
        }
        for(Ticket t : train.getBookedTickets()){
            Integer boookedfromIndex  = route.indexOf(t.getFromStation().toString());
            Integer bookedtoIndex = route.indexOf(t.getToStation().toString());
            if(boookedfromIndex <= fromStationIndex && bookedtoIndex >= toStationIndex
                    || boookedfromIndex >= fromStationIndex && boookedfromIndex < toStationIndex //without last 2 cond in if also this code works fine
                    || bookedtoIndex > fromStationIndex && bookedtoIndex <= toStationIndex){
                booked += t.getPassengersList().size();
            }
        }

        if(booked+bookTicketEntryDto.getNoOfSeats()> train.getNoOfSeats()){
            throw new Exception("Less tickets are available");
        }
        Ticket ticket = new Ticket();

        Passenger bookingPassenger = passengerRepository.findById(bookTicketEntryDto.getBookingPersonId()).get();
        List<Passenger> passengerList = new ArrayList<>();
        for(Integer id : bookTicketEntryDto.getPassengerIds()){
            Passenger passenger = passengerRepository.findById(id).get();
            passengerList.add(passenger);
        }
        ticket.setPassengersList(passengerList);
        ticket.setFromStation(bookTicketEntryDto.getFromStation());
        ticket.setToStation(bookTicketEntryDto.getToStation());
        ticket.setTotalFare(300 * (toStationIndex - fromStationIndex));
        bookingPassenger.getBookedTickets().add(ticket);
        ticket.setTrain(train);
        ticket = ticketRepository.save(ticket);
        train.getBookedTickets().add(ticket);
        trainRepository.save(train);
       return ticket.getTicketId();

    }
}
