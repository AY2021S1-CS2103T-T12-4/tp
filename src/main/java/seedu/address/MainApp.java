package seedu.address;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import seedu.address.commons.core.Config;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.Version;
import seedu.address.commons.exceptions.DataConversionException;
import seedu.address.commons.util.ConfigUtil;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Logic;
import seedu.address.logic.LogicManager;
import seedu.address.model.AppointmentBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.PatientBook;
import seedu.address.model.ReadOnlyAppointmentBook;
import seedu.address.model.ReadOnlyPatientBook;
import seedu.address.model.ReadOnlyUserPrefs;
import seedu.address.model.UserPrefs;
import seedu.address.model.util.SampleDataUtil;
import seedu.address.storage.AppointmentBookStorage;
import seedu.address.storage.JsonAppointmentBookStorage;
import seedu.address.storage.JsonPatientBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.PatientBookStorage;
import seedu.address.storage.Storage;
import seedu.address.storage.StorageManager;
import seedu.address.storage.UserPrefsStorage;
import seedu.address.ui.Ui;
import seedu.address.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final Version VERSION = new Version(1, 2, 1, true);

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;
    protected Config config;

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing Nuudle ]===========================");
        super.init();

        AppParameters appParameters = AppParameters.parse(getParameters());
        config = initConfig(appParameters.getConfigPath());

        UserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(config.getUserPrefsFilePath());
        UserPrefs userPrefs = initPrefs(userPrefsStorage);

        PatientBookStorage patientBookStorage = new JsonPatientBookStorage(userPrefs.getPatientBookFilePath());
        AppointmentBookStorage appointmentBookStorage =
                new JsonAppointmentBookStorage(userPrefs.getAppointmentBookFilePath());
        storage = new StorageManager(patientBookStorage, appointmentBookStorage, userPrefsStorage);

        initLogging(config);

        model = initModelManager(storage, userPrefs);

        logic = new LogicManager(model, storage);

        ui = new UiManager(logic);
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s patient book,
     * {@code storage}'s appointment book and {@code userPrefs}.
     */
    private Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        ReadOnlyPatientBook initialPatientData = initPatientBookModel(storage);
        ReadOnlyAppointmentBook initialAppointmentData = initAppointmentBookModel(storage);

        // Check if model is in sync
        if (!ModelManager.isValidModel(initialPatientData, initialAppointmentData)) {
            logger.warning("Appointment data not in sync with Patients' data. "
                    + "Will be starting with an empty AppointmentBook");
            initialAppointmentData = new AppointmentBook();
        }

        return new ModelManager(initialPatientData, initialAppointmentData, userPrefs);
    }

    /**
     * Returns a {@code ReadOnlyPatientBook} with the data from {@code storage}'s patient book.<br>
     * The data from the sample patient book will be used instead if {@code storage}'s patient book is not found,
     * or an empty patient book will be used instead if errors occur when reading {@code storage}'s patient book.
     */
    private ReadOnlyPatientBook initPatientBookModel(Storage storage) {
        Optional<ReadOnlyPatientBook> patientBookOptional;
        ReadOnlyPatientBook initialPatientData;

        try {
            patientBookOptional = storage.readPatientBook();
            if (patientBookOptional.isEmpty()) {
                logger.info("Data file not found. Will be starting with a sample PatientBook");
            }
            initialPatientData = patientBookOptional.orElseGet(SampleDataUtil::getSamplePatientBook);
        } catch (DataConversionException e) {
            logger.warning("Data file not in the correct format. Will be starting with an empty PatientBook");
            initialPatientData = new PatientBook();
        } catch (IOException e) {
            logger.warning("Problem while reading from the file. Will be starting with an empty PatientBook");
            initialPatientData = new PatientBook();
        }

        return initialPatientData;
    }

    /**
     * Returns a {@code ReadOnlyAppointmentBook} with the data from {@code storage}'s appointment book.<br>
     * The data from the sample appointment book will be used instead if {@code storage}'s appointment book
     * is not found, or an empty appointment book will be used instead if errors occur when reading
     * {@code storage}'s appointment book.
     */
    private ReadOnlyAppointmentBook initAppointmentBookModel(Storage storage) {
        Optional<ReadOnlyAppointmentBook> appointmentBookOptional;
        ReadOnlyAppointmentBook initialAppointmentData;

        try {
            appointmentBookOptional = storage.readAppointmentBook();
            if (appointmentBookOptional.isEmpty()) {
                logger.info("Data file not found. Will be starting with a sample AppointmentBook");
            }

            initialAppointmentData = appointmentBookOptional.orElseGet(SampleDataUtil::getSampleAppointmentBook);
        } catch (DataConversionException e) {
            logger.warning("Data file not in the correct format. Will be starting with an empty AppointmentBook");
            initialAppointmentData = new AppointmentBook();
        } catch (IOException e) {
            logger.warning("Problem while reading from the file. Will be starting with an empty AppointmentBook");
            initialAppointmentData = new AppointmentBook();
        }

        return initialAppointmentData;
    }

    private void initLogging(Config config) {
        LogsCenter.init(config);
    }

    /**
     * Returns a {@code Config} using the file at {@code configFilePath}. <br>
     * The default file path {@code Config#DEFAULT_CONFIG_FILE} will be used instead
     * if {@code configFilePath} is null.
     */
    protected Config initConfig(Path configFilePath) {
        Config initializedConfig;
        Path configFilePathUsed;

        configFilePathUsed = Config.DEFAULT_CONFIG_FILE;

        if (configFilePath != null) {
            logger.info("Custom Config file specified " + configFilePath);
            configFilePathUsed = configFilePath;
        }

        logger.info("Using config file : " + configFilePathUsed);

        try {
            Optional<Config> configOptional = ConfigUtil.readConfig(configFilePathUsed);
            initializedConfig = configOptional.orElse(new Config());
        } catch (DataConversionException e) {
            logger.warning("Config file at " + configFilePathUsed + " is not in the correct format. "
                    + "Using default config properties");
            initializedConfig = new Config();
        }

        //Update config file in case it was missing to begin with or there are new/unused fields
        try {
            ConfigUtil.saveConfig(initializedConfig, configFilePathUsed);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }
        return initializedConfig;
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(UserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using prefs file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataConversionException e) {
            logger.warning("UserPrefs file at " + prefsFilePath + " is not in the correct format. "
                    + "Using default user prefs");
            initializedPrefs = new UserPrefs();
        } catch (IOException e) {
            logger.warning("Problem while reading from the file. Will be starting with an empty PatientBook");
            initializedPrefs = new UserPrefs();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save config file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting Nuudle " + MainApp.VERSION);
        ui.start(primaryStage);
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping Nuudle ] =============================");
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }
}
