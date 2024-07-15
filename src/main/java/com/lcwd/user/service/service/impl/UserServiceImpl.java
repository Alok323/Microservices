package com.lcwd.user.service.service.impl;

import com.lcwd.user.service.entities.Hotel;
import com.lcwd.user.service.entities.Rating;
import com.lcwd.user.service.entities.User;
import com.lcwd.user.service.exception.ResourceNotFoundException;
import com.lcwd.user.service.external.services.HotelService;
import com.lcwd.user.service.repositories.UserRepository;
import com.lcwd.user.service.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HotelService hotelService;


    private Logger logger= LoggerFactory.getLogger(UserServiceImpl.class);





    // save
    @Override
    public User saveUser(User user) {
        //generate unique userId
        String randomUserId = UUID.randomUUID().toString();
        user.setUserId(randomUserId);
        return userRepository.save(user);
    }
    // get all
    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }
    //get single user
    @Override
    public User getUser(String userId) {
        // get user from database with the help of userRepository
        User user= userRepository.findById(userId).orElseThrow(
                ()->new ResourceNotFoundException("User with given id is not found on server !! :"+userId));
        //fetch rating of the above user from rating service
        //http://localhost:8083/ratings/users/33cdfef2-ba1e-4e45-aea6-ae55183fcaca

        Rating[] ratingsOfUser = restTemplate.getForObject(
                "http://RATING-SERVICE/ratings/users/"+user.getUserId(), Rating[].class);

        logger.info("{} ",ratingsOfUser);

        List<Rating> ratings = Arrays.stream(ratingsOfUser).toList();

        List<Rating> ratingList = ratings.stream().map(rating -> {
            //Api to call the hotel service to get the hotel
            //http://localhost:8082/hotels/756cd346-3c87-4b81-bf8f-d7db64b1bea3

//            ResponseEntity<Hotel> forEntity = restTemplate.getForEntity(
//                    "http://HOTEL-SERVICE/hotels/"+rating.getHotelId(), Hotel.class);
            Hotel hotel = hotelService.getHotel(rating.getHotelId());
//            logger.info("response status code: {} ",forEntity.getStatusCode());

            //set the hotel to rating
            rating.setHotel(hotel);

            //return the rating
            return rating;

        }).collect(Collectors.toList());


        user.setRatings(ratingList);


        return user;
    }
}
