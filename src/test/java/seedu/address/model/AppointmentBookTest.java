package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalAppointments.ALICE_APPOINTMENT;
import static seedu.address.testutil.TypicalAppointments.getTypicalAppointmentBook;
import static seedu.address.testutil.TypicalPatients.BENSON;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.appointment.exceptions.OverlappingAppointmentException;
import seedu.address.testutil.AppointmentBuilder;

public class AppointmentBookTest {

    private final AppointmentBook appointmentBook = new AppointmentBook();

    @Test
    public void constructor() {
        assertEquals(Collections.emptyList(), appointmentBook.getAppointmentList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> appointmentBook.resetData(null));
    }


    @Test
    public void resetData_withValidReadOnlyPatientBook_replacesData() {
        AppointmentBook newData = getTypicalAppointmentBook();
        appointmentBook.resetData(newData);
        assertEquals(newData, appointmentBook);
    }

    @Test
    public void resetData_withOverlappingAppointments_throwsOverlappingAppointmentException() {
        // Two patients with the overlapping appointments.
        Appointment editedAppointmentOne = new AppointmentBuilder(ALICE_APPOINTMENT)
                .withTime(LocalTime.of(9, 30)).build();
        List<Appointment> newAppointments = Arrays.asList(ALICE_APPOINTMENT, editedAppointmentOne);
        AppointmentBookStub newData = new AppointmentBookStub(newAppointments);

        assertThrows(OverlappingAppointmentException.class, () -> appointmentBook.resetData(newData));
    }

    @Test
    public void hasAppointment_nullAppointment_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> appointmentBook.hasAppointment(null));
    }

    @Test
    public void hasAppointment_nullAppointment_returnsFalse() {
        assertFalse(appointmentBook.hasAppointment(ALICE_APPOINTMENT));
    }

    @Test
    public void hasAppointment_appointmentInAppointmentBook_returnsTrue() {
        appointmentBook.addAppointment(ALICE_APPOINTMENT);
        assertTrue(appointmentBook.hasAppointment(ALICE_APPOINTMENT));
    }

    @Test
    public void hasAppointment_appointmentWithOverlapsInPatientBook_returnsTrue() {
        appointmentBook.addAppointment(ALICE_APPOINTMENT);

        Appointment overlappingAppointment = new AppointmentBuilder(ALICE_APPOINTMENT).withPatient(BENSON)
                .withTime(ALICE_APPOINTMENT.getStartTime().getTime().plusMinutes(1))
                .build();
        assertTrue(appointmentBook.hasOverlapsWith(overlappingAppointment));

        overlappingAppointment = new AppointmentBuilder(ALICE_APPOINTMENT).withPatient(BENSON)
                .withTime(ALICE_APPOINTMENT.getStartTime().getTime().plusMinutes(1),
                        ALICE_APPOINTMENT.getEndTime().getTime().minusMinutes(1))
                .build();
        assertTrue(appointmentBook.hasOverlapsWith(overlappingAppointment));
    }

    @Test
    public void getAppointmentList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> appointmentBook.getAppointmentList().remove(0));
    }

    /**
     * A stub ReadOnlyAppointmentBook whose appointments list can violate interface constraints.
     */
    private static class AppointmentBookStub implements ReadOnlyAppointmentBook {
        private final ObservableList<Appointment> appointments = FXCollections.observableArrayList();

        AppointmentBookStub(Collection<Appointment> patients) {
            this.appointments.setAll(patients);
        }

        @Override
        public ObservableList<Appointment> getAppointmentList() {
            return appointments;
        }
    }

}
