package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_PATIENT_DISPLAYED_INDEX;
import static seedu.address.logic.commands.CommandTestUtil.OVERLAP_TIME;
import static seedu.address.logic.commands.CommandTestUtil.SAME_TIME;
import static seedu.address.logic.commands.CommandTestUtil.VALID_DATE;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TIME;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_APPOINTMENT;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_APPOINTMENT;
import static seedu.address.testutil.TypicalPatients.ALICE;
import static seedu.address.testutil.TypicalPatients.getTypicalPatientBook;
import static seedu.address.testutil.TypicalPatients.getTypicalPatients;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.parser.TimeParserUtil;
import seedu.address.model.AppointmentBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.PatientBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.appointment.Appointment;
import seedu.address.model.appointment.Time;
import seedu.address.testutil.AppointmentBookBuilder;
import seedu.address.testutil.DateTimeLoaderBuilder;


public class AssignCommandTest {

    private final Model model = new ModelManager(getTypicalPatientBook(), new AppointmentBook(), new UserPrefs());

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        DateTimeLoader dateTimeLoader = new DateTimeLoader();
        assertThrows(NullPointerException.class, () -> new AssignCommand(null, dateTimeLoader));
    }

    @Test
    public void constructor_nullBuilder_throwsNullPointerException() {
        Index index = Index.fromZeroBased(10);
        assertThrows(NullPointerException.class, () -> new AssignCommand(index, null));
    }

    @Test
    public void constructor_nullIndexAndBuilder_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AssignCommand(null, null));
    }

    @Test
    public void execute_appointmentAcceptedByModel_assignSuccessful() {
        DateTimeLoader loader = new DateTimeLoaderBuilder().withDate(VALID_DATE).withTime(VALID_TIME).build();
        AssignCommand assignCommand = new AssignCommand(INDEX_FIRST_APPOINTMENT, loader);
        Appointment appointment = new Appointment(loader.getDate().get(), loader.getTime().get(), ALICE);

        String expectedMessage = String.format(AssignCommand.MESSAGE_SUCCESS, appointment);
        AppointmentBook expectedAppointmentBook = new AppointmentBookBuilder().withAppointment(appointment).build();

        Model expectedModel = new ModelManager(new PatientBook(model.getPatientBook()),
                expectedAppointmentBook, new UserPrefs());

        assertCommandSuccess(assignCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_appointmentRejectedDueToOverSizedIndex_failure() {
        Index overSizedIndex = Index.fromOneBased(getTypicalPatients().size() + 1);
        DateTimeLoader loader = new DateTimeLoaderBuilder().withDate(VALID_DATE).withTime(VALID_TIME).build();
        AssignCommand assignCommand = new AssignCommand(overSizedIndex, loader);

        assertCommandFailure(assignCommand, model, MESSAGE_INVALID_PATIENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_sameAppointmentRejectedDueToOverlap_failure() throws Exception {
        DateTimeLoader loader = new DateTimeLoaderBuilder().withDate(VALID_DATE).withTime(VALID_TIME).build();
        AssignCommand assignCommand = new AssignCommand(INDEX_FIRST_APPOINTMENT, loader);
        Appointment appointment = new Appointment(
                loader.getDate().get(), new Time(TimeParserUtil.parse(SAME_TIME)), ALICE
        );

        model.addAppointment(appointment);

        assertCommandFailure(assignCommand, model, AssignCommand.ASSIGNMENT_OVERLAP);
    }

    @Test
    public void execute_differentAppointmentRejectedDueToOverlap_failure() throws Exception {
        DateTimeLoader loader = new DateTimeLoaderBuilder().withDate(VALID_DATE).withTime(VALID_TIME).build();
        AssignCommand assignCommand = new AssignCommand(INDEX_FIRST_APPOINTMENT, loader);
        Appointment appointment = new Appointment(
                loader.getDate().get(), new Time(TimeParserUtil.parse(OVERLAP_TIME)), ALICE
        );

        model.addAppointment(appointment);

        assertCommandFailure(assignCommand, model, AssignCommand.ASSIGNMENT_OVERLAP);
    }

    @Test
    public void equals() {
        DateTimeLoader loader = new DateTimeLoaderBuilder().withDate(VALID_DATE).withTime(VALID_TIME).build();
        DateTimeLoader diffLoader = new DateTimeLoaderBuilder().withDate(VALID_DATE).withTime(OVERLAP_TIME).build();
        AssignCommand command = new AssignCommand(INDEX_FIRST_APPOINTMENT, loader);

        // same values -> returns true
        DateTimeLoader loaderCopy = new DateTimeLoaderBuilder().withDate(VALID_DATE).withTime(VALID_TIME).build();
        AssignCommand commandCopy = new AssignCommand(INDEX_FIRST_APPOINTMENT, loaderCopy);
        assertTrue(command.equals(commandCopy));

        // same object -> returns true
        assertTrue(command.equals(command));

        // null -> returns false
        assertFalse(command.equals(null));

        //different types -> returns false
        assertFalse(command.equals(new ClearCommand()));

        // different index -> returns false
        AssignCommand diffIndexCommand = new AssignCommand(INDEX_SECOND_APPOINTMENT, loader);
        assertFalse(command.equals(diffIndexCommand));

        // different dateTimeLoader -> returns false
        AssignCommand diffLoaderCommand = new AssignCommand(INDEX_FIRST_APPOINTMENT, diffLoader);
        assertFalse(command.equals(diffLoaderCommand));
    }
}
