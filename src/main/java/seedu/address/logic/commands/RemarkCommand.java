package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;

/**
 * Changes the remark of an existing person in the address book
 */
public class RemarkCommand extends Command {

    public static final String COMMAND_WORD = "remark";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the remark of the person identified"
            + "by the index number used in the last person listing. "
            + "Existing remark will be overwritten by the input. \n"
            + "Paramaters: INDEX (must be a positive integer) "
            + "r/ [REMARK]\n"
            + "Example: " + COMMAND_WORD + " 1 "
            + "r/ Likes to swim.";

    public static final String MESSAGE_ADD_REMARK_SUCCESS = "Added remark to person: %1$s";
    public static final String MESSAGE_REMOVE_REMARK_SUCCESS = "Removed remark from person: %1$s";

    private final Index index;
    private final Remark remark;

    /**
     * @param index  of the person in the filtered person list to edit the remark
     * @param remark of the person to be updated to
     */
    public RemarkCommand(Index index, Remark remark) {
        requireAllNonNull(index, remark);

        this.index = index;
        this.remark = remark;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Person personWithUpdatedRemark = updateRemark(personToEdit, remark);

        model.setPerson(personToEdit, personWithUpdatedRemark);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        return new CommandResult(generateSuccessMessage(personWithUpdatedRemark));
    }

    /**
     * Generate a command execution success message depending on whether a remark is added or removed.
     *
     * @param editedPerson the Person with the updated remark
     * @return the command execution success message
     */
    private String generateSuccessMessage(Person editedPerson) {
        String successMsg = remark.isEmpty() ? MESSAGE_REMOVE_REMARK_SUCCESS : MESSAGE_ADD_REMARK_SUCCESS;
        return String.format(successMsg, editedPerson);
    }

    /**
     * Returns a new Person with the new updated remark.
     *
     * @param personToEdit the Person to be updated
     * @param remark       the remark to be added
     * @return the new updated Person
     */
    private Person updateRemark(Person personToEdit, Remark remark) {
        return new Person(personToEdit.getName(), personToEdit.getPhone(),
                personToEdit.getEmail(), personToEdit.getAddress(),
                personToEdit.getTags(), remark);
    }


    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof RemarkCommand)) {
            return false;
        }

        // state check
        RemarkCommand e = (RemarkCommand) other;
        return index.equals(e.index)
                && remark.equals(e.remark);
    }
}
