package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.logic.commands.RemarkCommand.MESSAGE_ADD_REMARK_SUCCESS;
import static seedu.address.logic.commands.RemarkCommand.MESSAGE_REMOVE_REMARK_SUCCESS;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.testutil.PersonBuilder;

public class RemarkCommandTest {
    private static final String DUMMY_REMARK = "dummy";

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_addRemarkUnfilteredList_success() {
        Person originalPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(originalPerson).withRemark(DUMMY_REMARK).build();

        RemarkCommand remarkCommand = new RemarkCommand(INDEX_FIRST_PERSON, new Remark(DUMMY_REMARK));
        String expectedMessage = String.format(MESSAGE_ADD_REMARK_SUCCESS, editedPerson);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(originalPerson, editedPerson);

        assertCommandSuccess(remarkCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_removeRemarkUnfilteredList_success() {
        Person originalPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(originalPerson).withRemark("").build();

        RemarkCommand remarkCommand = new RemarkCommand(INDEX_FIRST_PERSON, Remark.makeEmpty());
        String expectedMessage = String.format(MESSAGE_REMOVE_REMARK_SUCCESS, editedPerson);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(originalPerson, editedPerson);

        assertCommandSuccess(remarkCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_addRemarkFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person originalPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(originalPerson).withRemark(DUMMY_REMARK).build();

        RemarkCommand remarkCommand = new RemarkCommand(INDEX_FIRST_PERSON, new Remark(DUMMY_REMARK));
        String expectedMessage = String.format(MESSAGE_ADD_REMARK_SUCCESS, editedPerson);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(originalPerson, editedPerson);

        assertCommandSuccess(remarkCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_removeRemarkFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person originalPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(originalPerson).withRemark("").build();

        RemarkCommand remarkCommand = new RemarkCommand(INDEX_FIRST_PERSON, Remark.makeEmpty());
        String expectedMessage = String.format(MESSAGE_REMOVE_REMARK_SUCCESS, editedPerson);

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(originalPerson, editedPerson);

        assertCommandSuccess(remarkCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromZeroBased(model.getFilteredPersonList().size());
        RemarkCommand remarkCommand = new RemarkCommand(outOfBoundIndex, new Remark(DUMMY_REMARK));

        assertCommandFailure(remarkCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Index outOfBoundIndex = Index.fromZeroBased(model.getFilteredPersonList().size());
        RemarkCommand remarkCommand = new RemarkCommand(outOfBoundIndex, new Remark(DUMMY_REMARK));

        assertCommandFailure(remarkCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);

    }

    @Test
    public void equals() {
        RemarkCommand baseCommand = new RemarkCommand(INDEX_FIRST_PERSON, new Remark(DUMMY_REMARK));

        // Same index and remark value --> true
        assertEquals(new RemarkCommand(INDEX_FIRST_PERSON, new Remark(DUMMY_REMARK)), baseCommand);

        // Same object --> true
        assertEquals(baseCommand, baseCommand);

        // Equal null --> false
        assertNotEquals(baseCommand, null);

        // Different command type --> false
        assertNotEquals(new ClearCommand(), baseCommand);

        // Different index --> false
        assertNotEquals(new RemarkCommand(INDEX_SECOND_PERSON, new Remark(DUMMY_REMARK)), baseCommand);

        // Different remark --> false
        assertNotEquals(new RemarkCommand(INDEX_FIRST_PERSON, Remark.makeEmpty()), baseCommand);
    }
}
