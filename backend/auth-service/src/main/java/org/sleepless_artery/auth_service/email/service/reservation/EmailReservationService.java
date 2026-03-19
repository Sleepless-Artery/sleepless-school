package org.sleepless_artery.auth_service.email.service.reservation;

import java.time.Duration;


/**
 * Service interface for managing temporary email reservations in Redis.
 */
public interface EmailReservationService {

    boolean isEmailAddressAvailable(String emailAddress);

    void reserveEmailAddress(String emailAddress, Object reservationData, Duration reservationDuration);

    void checkReservation(String emailAddress, String confirmationCode);
}
