package com.irctc.repository;

import com.irctc.model.Booking;
import com.irctc.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsBySeatsAndTravelDate(Seat seat, LocalDate travelDate);
    List<Booking> findByUserId(Long userId);
    Optional<Booking> findByPnr(String pnr);

    boolean existsByStripeSessionId(String stripeSessionId);

}