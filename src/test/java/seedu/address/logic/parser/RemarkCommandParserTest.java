package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.RemarkCommand;
import seedu.address.model.person.Remark;

public class RemarkCommandParserTest {

    private static final String DUMMY_REMARK_VALUE = "dummy";
    private RemarkCommandParser parser = new RemarkCommandParser();

    @Test
    public void parse_addRemark_success() {
        // add remark
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + " " + PREFIX_REMARK + DUMMY_REMARK_VALUE;
        RemarkCommand expectedCommand = new RemarkCommand(targetIndex, new Remark(DUMMY_REMARK_VALUE));
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_removeRemark_success() {
        // remove remark using empty remark
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + " " + PREFIX_REMARK;
        RemarkCommand expectedCommand = new RemarkCommand(targetIndex, Remark.makeEmpty());
        assertParseSuccess(parser, userInput, expectedCommand);

        // remove remark without prefix
        userInput = Integer.toString(targetIndex.getOneBased());
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_missingField_failure() {
        String expectedMsg = String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                RemarkCommand.MESSAGE_USAGE);

        // no index specified
        assertParseFailure(parser, DUMMY_REMARK_VALUE, expectedMsg);

        // no parameters
        assertParseFailure(parser, "", expectedMsg);
    }
}
