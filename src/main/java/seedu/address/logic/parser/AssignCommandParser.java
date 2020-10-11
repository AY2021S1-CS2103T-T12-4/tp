package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.core.Messages.MESSAGE_EXPIRED_DATE_TIME;
import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIME;
import static seedu.address.model.appointment.Appointment.CREATION_OFFSET_MINUTES;

import java.time.LocalTime;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.DateTimeUtil;
import seedu.address.logic.commands.AssignCommand;
import seedu.address.logic.commands.AssignCommand.DateTimeLoader;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.appointment.Date;
import seedu.address.model.appointment.Time;


/**
 * Parses input arguments and creates a new AssignCommand object
 */
public class AssignCommandParser implements Parser<AssignCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AssignCommand
     * and returns an AssignCommand object for execution.
     *
     * @throws ParseException if the user input does not conform the expected format
     */
    public AssignCommand parse(String arg) throws ParseException {
        requireNonNull(arg);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(arg, PREFIX_DATE, PREFIX_TIME);

        Index index;

        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AssignCommand.MESSAGE_USAGE), pe);
        }

        if (argMultimap.getValue(PREFIX_DATE).isEmpty()) {
            throw new ParseException(AssignCommand.DATE_MISSING);
        }
        if (argMultimap.getValue(PREFIX_TIME).isEmpty()) {
            throw new ParseException(AssignCommand.TIME_MISSING);
        }

        DateTimeLoader dateTimeLoader = new DateTimeLoader();
        Date date = ParserUtil.parseDate(argMultimap.getValue(PREFIX_DATE).get());
        Time time = ParserUtil.parseTime(argMultimap.getValue(PREFIX_TIME).get());

        // Expired date time can only be confirmed when date and time are put together.
        LocalTime timeWithLeeway = time.getTime().plusMinutes(CREATION_OFFSET_MINUTES);
        if (DateTimeUtil.isExpired(date.getDate(), timeWithLeeway)) {
            throw new ParseException(MESSAGE_EXPIRED_DATE_TIME);
        }

        dateTimeLoader.setAppointmentDate(date);
        dateTimeLoader.setAppointmentTime(time);

        return new AssignCommand(index, dateTimeLoader);
    }
}
