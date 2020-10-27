package seedu.address.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.FileUtil;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.ReadOnlyAppointmentBook;

/**
 * A class to access AppointmentBook data stored as a json file on the hard disk.
 */
public class JsonAppointmentBookStorage implements AppointmentBookStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonAppointmentBookStorage.class);

    private AppointmentArchive csvArchive;

    private Path filePath;

    /**
     * @param filePath the filePath for the appointment storage.
     * @param archivePath the directory path for the appointment archives.
     */
    public JsonAppointmentBookStorage(Path filePath, Path archivePath) {
        this.filePath = filePath;
        this.csvArchive = new CsvAppointmentArchive(archivePath);
    }

    public Path getAppointmentBookFilePath() {
        return filePath;
    }

    /**
     * Returns the {@code Path} to the archive directory.
     */
    public Path getAppointmentArchiveDirPath() {
        return csvArchive.getArchiveDirectoryPath();
    }

    @Override
    public Optional<ReadOnlyAppointmentBook> readAppointmentBook() throws DataConversionException {
        return readAppointmentBook(filePath);
    }

    /**
     * Similar to {@link #readAppointmentBook()}.
     *
     * @param filePath location of the data. Cannot be null.
     * @throws DataConversionException if the file is not in the correct format.
     */
    public Optional<ReadOnlyAppointmentBook> readAppointmentBook(Path filePath) throws DataConversionException {
        requireNonNull(filePath);

        Optional<JsonSerializableAppointmentBook> jsonAppointmentBook = JsonUtil.readJsonFile(
                filePath, JsonSerializableAppointmentBook.class);
        if (jsonAppointmentBook.isEmpty()) {
            return Optional.empty();
        }

        try {
            return Optional.of(jsonAppointmentBook.get().toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataConversionException(ive);
        }
    }

    @Override
    public ReadOnlyAppointmentBook archivePastAppointments(ReadOnlyAppointmentBook appointmentBook) {
        return csvArchive.archivePastAppointments(appointmentBook);
    }

    @Override
    public void saveAppointmentBook(ReadOnlyAppointmentBook appointmentBook) throws IOException {
        saveAppointmentBook(appointmentBook, filePath);
    }

    /**
     * Similar to {@link #saveAppointmentBook(ReadOnlyAppointmentBook)}.
     *
     * @param filePath location of the data. Cannot be null.
     */
    public void saveAppointmentBook(ReadOnlyAppointmentBook appointmentBook, Path filePath) throws IOException {
        requireNonNull(appointmentBook);
        requireNonNull(filePath);

        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableAppointmentBook(appointmentBook), filePath);
    }

    @Override
    public String getArchiveStatus() {
        return csvArchive.getArchiveStatistics();
    }

}
